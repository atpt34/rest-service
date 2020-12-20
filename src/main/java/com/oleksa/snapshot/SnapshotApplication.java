package com.oleksa.snapshot;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.proxy.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class SnapshotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnapshotApplication.class, args);
	}

	@Bean
			@Lazy
//	@Order(value = 5000)
	CommandLineRunner cmd1() {
		return args -> {
			System.out.println("cmd 1");
		};
	}

	@Bean
//	@Order(value = 50000)
	ApplicationRunner cmd2() {
		return args -> {
			System.out.println("cmd 2");
//			HttpClient httpClient = HttpClient.newHttpClient();
//			httpClient.send()
		};
	}

//	@Bean
//	public WebSecurityConfigurerAdapter webSecurity() {
//		return new WebSecurityConfigurerAdapter() {
//			@Override
//			protected void configure(HttpSecurity httpSecurity) throws Exception {
//				httpSecurity.antMatcher("/**")
//                        .authorizeRequests()
//                        .anyRequest()
//                        .permitAll();
//			}
//		};
//	}

    @Bean
    public RandomCaller randomCaller() {
        Callback callback = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("method: " + method.getName());
                if ("doNew".equals(method.getName())) {
					StopWatch watch = new StopWatch();
					try {
						watch.start();
						return methodProxy.invokeSuper(obj, args);
					} finally {
						watch.stop();
						System.out.println("doNew intercept took: " + watch.getLastTaskTimeMillis());
					}
                }
                return methodProxy.invokeSuper(obj, args);
            }
        };
//        Callback callback = new InvocationHandler() {
//            @Override
//            public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
//                System.out.println("proxied: " + method.getName());
//                return method.invoke(obj, args);
//            }
//        };

        RandomCaller rc = (RandomCaller) Enhancer.create(RandomCaller.class, callback);
//        Enhancer enhancer = new Enhancer();
//        enhancer.setCallback(callback);
//        RandomCaller rc = (RandomCaller) enhancer.create(new Class[]{RandomCaller.class}, new Object[]{new RandomCaller()});
        rc.monitorMethod("abc", () -> System.out.println("hello booom"));
        return rc;
    }

    @Bean
    CommandLineRunner cmd(RestHighLevelClient client) {
		return args -> {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//			searchSourceBuilder.aggregation(AggregationBuilders.terms("top_10_states").field("state").size(10));

			SearchRequest searchRequest = new SearchRequest();
			searchRequest.indices("english");
			searchRequest.source(searchSourceBuilder);
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			System.out.println(searchResponse);
		};
	}
}
