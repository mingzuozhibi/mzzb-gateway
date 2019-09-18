package mingzuozhibi.gateway.security;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebSecurityHandler webSecurityHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

            .authorizeRequests()
            .antMatchers("/gateway/session").permitAll()
            .antMatchers("/gateway/register").permitAll()
            .antMatchers(HttpMethod.GET).permitAll()
            .antMatchers("/gateway/**").hasRole("Login")

            .and().anonymous()
            .principal("Guest")
            .authorities("ROLE_Guest")

            .and().exceptionHandling()
            .accessDeniedHandler(webSecurityHandler)
            .authenticationEntryPoint(webSecurityHandler)

            .and().csrf()
            .ignoringAntMatchers("/gateway/session")
            .ignoringAntMatchers("/gateway/register")

            .and().addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);
    }

}
