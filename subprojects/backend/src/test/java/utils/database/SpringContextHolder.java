package utils.database;

import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

public class SpringContextHolder {

    private static ApplicationContext context = null;

    public static void setContext(ApplicationContext context) {
        SpringContextHolder.context = context;
    }

    public static ApplicationContext getContext() {
        validate();
        return context;
    }

    public static SessionFactory getEntityBuilderSessionFactory() {
        validate();
        return context.getBean("entityBuilderSessionFactory", SessionFactory.class);
    }

    private static void validate() {
        if (context == null) {
            throw new RuntimeException("Set Spring context to SpringContextHolder with setContext method.");
        }
    }
}
