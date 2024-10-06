package kopo.poly.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${rootPath}")
    private String rootPath;
    @Value("${inputImgDir}")
    private String inputImgDir;
    @Value("${generatedImgDir}")
    private String generatedImgDir;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/inputImg/**")
                .addResourceLocations("file:"+rootPath+"/"+inputImgDir+"/");
        // C:/KPaaS/src/main/resources/static/inputImg

        registry.addResourceHandler("/generatedImages/**")
                        .addResourceLocations("file:"+rootPath+"/"+generatedImgDir+"/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/uploads/");
    }
}