//パッケージが属しているという宣言
package com.example.board.config;

//Springが管理するオブジェクト
import org.springframework.context.annotation.Bean;
//設定するクラス
import org.springframework.context.annotation.Configuration;
//セキュリティ規則を設定するツール
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//Webセキュリティ機能活性化
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

//Spring設定ファイルというアノテーション
@Configuration
//Springセキュリティ機能をONにする
@EnableWebSecurity
public class SecurityConfig {
    //Springが管理する
    @Bean
    //セキュリティ規則のチェイン
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //http要請に対しての権限設定が始まる部分
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        //誰がどんなURLにアクセスできるのか
                        .requestMatchers("/", "/signup","/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}

