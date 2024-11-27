//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
//
//import javax.sql.DataSource;
//
//@TestConfiguration
//@EnableJpaRepositories(basePackages = "com.creatorhub.domain")
//@EntityScan("com.creatorhub.domain")
//public class TestConfig {
//    @Bean
//    public JpaMetamodelMappingContext jpaMetamodelMappingContext() {
//        return new JpaMetamodelMappingContext();
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .build();
//    }
//}