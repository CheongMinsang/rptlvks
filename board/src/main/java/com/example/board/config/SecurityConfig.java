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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                //　csrf保護を無効化
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        //誰がどんなURLにアクセスできるのか
                        .requestMatchers("/", "/signup","/login","/logout","/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                )
                // H2接近許容
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                // ログアウトRedirect無効化
                .logout(logout -> logout.disable())
                // フォームログイン機能も無効化
                .formLogin(form -> form.disable())
                // HTTP Basic認証も無効化
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}

