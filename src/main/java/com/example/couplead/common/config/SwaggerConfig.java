@configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(
                    new Info()
                        .title("Couplead API")
                        .version("1.0.0")
                        .description("Couple Platform")
                );
    }
}
