```java
@Configuration
@EnableWebMvc
@ComponentScan("com.xja.hemiao.web.controller") // 包扫描
public class SpringWebConfig implements WebMvcConfigurer {

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/jsp");
		resolver.setSuffix(".jsp");
		return resolver;
	}

	@Bean(name = "multipartResolver") // bean必须写name属性且必须为multipartResolver
	protected CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setMaxUploadSize(5 * 1024 * 1024);
		commonsMultipartResolver.setMaxInMemorySize(0);
		commonsMultipartResolver.setDefaultEncoding("UTF-8");
		return commonsMultipartResolver;
	}

	// 静态资源的处理
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
			.addResourceHandler("/css/**")
			.addResourceLocations("classpath:/css");
        registry
			.addResourceHandler("/js/**")
			.addResourceLocations("classpath:/js");
        registry
			.addResourceHandler("/img/**")
			.addResourceLocations("classpath:/img");
    }

	@Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);

        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);

        converters.add(fastConverter);
    }
}
```
