environments {
 datasource.url='jdbc:mysql://localhost:3306/sope'
 datasource.username=''
 datasource.password='sope#'
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
        datasource.url='jdbc:mysql://192.167.1.1:3306/sope'
        datasource.password = ''
        datasource.username=''
    }
    
    prod {
        datasource.driverClassName='com.mysql.jdbc.Driver'
        datasource.url='jdbc:mysql://google/sope?cloudSqlInstance=sope-111111:europe-west1:sope-beta-test&socketFactory=com.google.cloud.sql.mysql.SocketFactory'
        datasource.password=''
        datasource.username=''
    }
}