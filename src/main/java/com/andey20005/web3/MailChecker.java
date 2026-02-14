package com.andey20005.web3;

import com.andey20005.web3.Area.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Named
@ApplicationScoped
public class MailChecker {
    private static final String MAIL_HOST = "imap.yandex.ru";
    private static final String MAIL_USER = "andr-for-tests@yandex.ru";
    // set JAVA_OPTS=-DMailChecker.mailPassword=...
    private static final String MAIL_PASSWORD = "...";

    private ScheduledExecutorService scheduler;

    @PersistenceContext(unitName = "pointsPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private Area area;
    {
        AndArea quarterCircle = new AndArea(new ArrayList<>());
        quarterCircle.addArea(new AboveLine(0, 0, 1, 0));
        quarterCircle.addArea(new AboveLine(0, 0, 0, 1));
        quarterCircle.addArea(new Circle(1, 0, 0));
        AndArea lowerTriangle = new AndArea(new ArrayList<>());
        lowerTriangle.addArea(new AboveLine(0, 0, 1, 0));
        lowerTriangle.addArea(new AboveLine(0, 0, 0, -1));
        lowerTriangle.addArea(new AboveLine(0, -0.5, -1, 1));
        AndArea rectangle = new AndArea(new ArrayList<>());
        rectangle.addArea(new AboveLine(0, 0, -1, 0));
        rectangle.addArea(new AboveLine(0, 0, 0, -1));
        rectangle.addArea(new AboveLine(-1, -1, 1, 0));
        rectangle.addArea(new AboveLine(-1, -1, 0, 1));
        OrArea orArea = new OrArea(new ArrayList<>());
        orArea.addArea(quarterCircle);
        orArea.addArea(lowerTriangle);
        orArea.addArea(rectangle);
        area = orArea;
    }

    @PostConstruct
    public void init() {
        AndArea quarterCircle = new AndArea(new ArrayList<>());
        quarterCircle.addArea(new AboveLine(0, 0, 1, 0));
        quarterCircle.addArea(new AboveLine(0, 0, 0, 1));
        quarterCircle.addArea(new Circle(1, 0, 0));
        AndArea lowerTriangle = new AndArea(new ArrayList<>());
        lowerTriangle.addArea(new AboveLine(0, 0, 1, 0));
        lowerTriangle.addArea(new AboveLine(0, 0, 0, -1));
        lowerTriangle.addArea(new AboveLine(0, -0.5, -1, 1));
        AndArea rectangle = new AndArea(new ArrayList<>());
        rectangle.addArea(new AboveLine(0, 0, -1, 0));
        rectangle.addArea(new AboveLine(0, 0, 0, -1));
        rectangle.addArea(new AboveLine(-1, -1, 1, 0));
        rectangle.addArea(new AboveLine(-1, -1, 0, 1));
        OrArea orArea = new OrArea(new ArrayList<>());
        orArea.addArea(quarterCircle);
        orArea.addArea(lowerTriangle);
        orArea.addArea(rectangle);
        area = orArea;
        System.out.println("Запуск проверки почты... \n пароль - " + MAIL_PASSWORD);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::checkMail,
                0,
                20,
                TimeUnit.SECONDS
        );
    }

    void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("=== MailChecker: приложение запущено ===");
    }

    @PreDestroy
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("Проверка почты остановлена");
        }
    }

    private void checkMail() {
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imap.host", MAIL_HOST);
            props.put("mail.imap.port", "993");
            props.put("mail.imap.ssl.enable", "true");
            // Чтобы не переподключаться каждый раз
            props.put("mail.imap.connectiontimeout", "5000");
            props.put("mail.imap.timeout", "5000");

            Session session = Session.getInstance(props);
            Store store = session.getStore();
            store.connect(MAIL_HOST, MAIL_USER, MAIL_PASSWORD);
            System.out.println("password - MAIL_PASSWORD");

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Получаем новые письма (не прочитанные)
            Message[] messages = inbox.search(
                    new FlagTerm(new Flags(Flags.Flag.SEEN), false)
            );

            System.out.println("Найдено новых писем: " + messages.length);

            for (Message message : messages) {
                try {
                    String subject = message.getSubject();
                    String from = message.getFrom()[0].toString();
                    String content = getTextFromMessage(message);
                    java.util.Date receivedDate = message.getReceivedDate();

                    System.out.println("Новое письмо: " + subject + " от " + from);

                    saveEmailToDatabase(subject, from, content);

                    message.setFlag(Flags.Flag.SEEN, true);
                } catch (Exception e) {
                    System.out.println("Ошибка обработки письма: " + e.getMessage());
                }
            }
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            System.out.println("Ошибка проверки почты: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
                break; // берем только текстовую часть
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                // Можно оставить как есть или конвертировать в plain text
                result.append(html);
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    private static final Pattern xPattern = Pattern.compile(" *x *= *([-\\d.,]+)");
    private static final Pattern yPattern = Pattern.compile(" *[yу] *= *([-\\d.,]+)");
    private static final Pattern rPattern = Pattern.compile(" *r *= *([-\\d.,]+)");

    @Transactional
    private void saveEmailToDatabase(String subject, String from, String content) {
        Matcher xMatcher = xPattern.matcher(content);
        Matcher yMatcher = yPattern.matcher(content);
        Matcher rMatcher = rPattern.matcher(content);
        if (xMatcher.find() && yMatcher.find() && rMatcher.find()) {
            try {
                double x = Double.parseDouble(xMatcher.group(1));
                double y = Double.parseDouble(yMatcher.group(1));
                double r = Double.parseDouble(rMatcher.group(1));
                Point p = new Point(x, y, r, area);
                try {
                    utx.begin();
                    em.persist(p);
                    utx.commit();
                    System.out.printf(
                            "\tполучилось сохранить: %s\n",
                            p
                    );
                } catch (Exception e) {
                    try {
                        if (utx != null && utx.getStatus() == Status.STATUS_ACTIVE) {
                            utx.rollback();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    System.err.println("Ошибка сохранения: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (RuntimeException e) {
                System.out.println("что-то поломалось");
                e.printStackTrace();
            }
        } else {
            System.out.println("ничего не нашлось");
        }
    }
}
