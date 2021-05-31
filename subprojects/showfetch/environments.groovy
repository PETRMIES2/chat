environments {
 datasource.url='jdbc:mysql://localhost:3306/database'
 datasource.username=''
 datasource.password=''
 datasource.driverClassName='com.mysql.jdbc.Driver'
 hibernate.dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
 hibernate.show_sql = 'false'
 hibernate.format_sql = 'false'
 smtp.host = ''
 smtp.port = ''

    dev {
    }

    cloud_mysql {
        datasource.driverClassName='com.mysql.jdbc.Driver'
        datasource.url='jdbc:mysql://XXX:3306/YYY'
        datasource.password = ''
        datasource.username=''
    }
        
    test {
    }
    
    prod {
    }
}