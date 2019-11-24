package com.thehecklers.ssecformskotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SsecFormsKotlinApplication

fun main(args: Array<String>) {
    runApplication<SsecFormsKotlinApplication>(*args)
}

@EnableWebSecurity
class SecurityConfig: WebSecurityConfigurerAdapter() {
    val pwEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun authentication(): InMemoryUserDetailsManager {
        val mark = User.builder()
            .username("mark")
            .password(pwEncoder.encode("badpassword"))
//            .password("badpassword")
            .roles("USER")
            .build();

        val joeg = User.builder()
            .username("joeg")
            .password(pwEncoder.encode("Much\$Better%Password"))
//            .password("Much\$Better%Password")
            .roles("USER", "ADMIN")
            .build();

        println("   Mark's password is ${mark.password}")
        println("   JoeG's password is ${joeg.password}")

        return InMemoryUserDetailsManager(mark, joeg);
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .mvcMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and().formLogin()
            .and().httpBasic()
    }
}

@RestController
class FormsController {
    @GetMapping
    fun everyone() = "Hello everybody!"

	@GetMapping("/admin")
	fun adminsOnly() = "<h1>Administrator Page</h1>Greetings, Admin!"
}
