package br.com.ster.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundTest {
	
	@Test
	public void testOlaMundo() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
//		System.out.println(response.getBody().asString().equals("Ola Mundo!"));
//		System.out.println(response.statusCode() == 200);
		
		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		Assert.assertTrue(response.statusCode() == 200);
		Assert.assertEquals(200, response.statusCode());
		
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
		
	}
	
	@Test
	public void devoConehcerOutrasFormasRestAssured() {
		// o get e o request são metodos staticos ctrl + shift + m adiciona o import
		Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
		
		get("http://restapi.wcaquino.me/ola").then().statusCode(200);
		
		// usando as anotações do restAssured
		given()  //pré condições
		.when()		//ação
			.get("http://restapi.wcaquino.me/ola")
		.then()	//assertivas
//			.assertThat()
			.statusCode(200);
	}
	
	@Test
	public void devoConhecerMachesHamcrest() {
		Assert.assertThat("Maria", Matchers.is("Maria"));
		Assert.assertThat(128, Matchers.is(128));
		Assert.assertThat(128, Matchers.isA(Integer.class));
		Assert.assertThat(128d, Matchers.isA(Double.class));
		Assert.assertThat(128d, Matchers.greaterThan(120d));
		Assert.assertThat(128d, Matchers.lessThan(130d));
		
		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		//Assert.assertThat(impares, Matchers.hasSize(5)); //verifica o tamanho da lista
		assertThat(impares, hasSize(5)); //verifica o tamanho da lista
		assertThat(impares, contains(1,3,5,7,9)); //verifica os tamanhos da lista com os elemetos nessa ordem
		assertThat(impares, containsInAnyOrder(1,5,3,7,9)); // verifica o que contem idependente da ordem
		assertThat(impares, hasItem(1)); // verifica se tem apenas o elemento 1
		assertThat(impares, hasItems(1,3)); // verifica se tem apenas mais de 1 elemento
		
		assertThat("Maria", is(not("Joao"))); //verifica que maria não é joao
		assertThat("Maria", not("Joao")); // outra opção verifica que maria não é joao
		assertThat("maria", anyOf(is("maria"), is("joaquina"))); // verifica se maria é uma das opções 
		assertThat("joaquina", allOf(startsWith("joa"), endsWith("ina"), containsString("qui"))); 
		// verifica se comoça com joa termina com ina e em algum lugar tem a string qui
		
	}
	
	@Test
	public void devoValidarBody() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
		.then()
			.statusCode(200)
			.body(is("Ola Mundo!"))
			.body(containsString("Mundo"))
			.body(is(not(nullValue())));
	}
	
	
	

}
