package springboot.jpa.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import springboot.jpa.service.MemberService;

@Configuration
@EnableWebSecurity // Spring Security 설정 클래스로 등록
@AllArgsConstructor
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    /**
     * 비밀번호 암호화를 위한 Bean
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http.authorizeRequests()
                // 페이지 권한 설정
                .antMatchers("/info").hasRole("MEMBER") // MEMBER, ADMIN만 접근 허용
                .antMatchers("/admin").hasRole("ADMIN") // ADMIN만 접근 허용
                .antMatchers("/**").permitAll() // 그외 모든 경로에 대해서는 권한 없이 접근 허용
                // .anyRequest().authenticated() // 나머지 요청들은 권한의 종류에 상관 없이 권한이 있어야 접근 가능
                .and() // 로그인 설정
                .formLogin()
                .loginPage("/members/login") // Custom login form 사용
                .failureUrl("/login-error") // 로그인 실패 시 이동
                .defaultSuccessUrl("/") // 로그인 성공 시 redirect 이동
                .and() // 로그아웃 설정
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // 로그아웃 시 URL 재정의
                .logoutSuccessUrl("/") // 로그아웃 성공 시 redirect 이동
                .invalidateHttpSession(true) // HTTP Session 초기화
                .deleteCookies("JSESSIONID") // 특정 쿠키 제거
                .and()
                // 403 예외처리 핸들링
                .exceptionHandling().accessDeniedPage("/denied");
        return http.build() ;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Security 설정
     * @param web FilterChainProxy 생성 필터
     * @throws Exception
     */
    public void configure(WebSecurity web) throws Exception
    {
        // Spring Security가 인증을 무시할 경로 설정
        web.ignoring().antMatchers("/css/**", "/img/**", "/js/**", "/lib/**", "/vendor/**");
    }
}