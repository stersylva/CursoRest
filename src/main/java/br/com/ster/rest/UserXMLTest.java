package br.com.ster.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.internal.path.xml.NodeImpl;




public class UserXMLTest {
	
	@Test
	//aula 18 e 19
	public void devoTrabalharComXML() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/usersXML/3")
		.then()
			.statusCode(200)
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			
			.detachRootPath("filhos")
			
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			
			.appendRootPath("filhos")
			.body("name", hasItem("Luizinho"))
			.body("name", hasItems("Luizinho", "Zezinho"))
		;
	}
	
	//aula 20
	@Test
	public void devoFazerPesquisasAvancadasComXML() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(200)
			.body("users.user.size()", is(3))
			.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
			.body("users.user.@id", hasItems("1", "2", "3"))
			.body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
			.body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
			.body("users.user.age.collect{it.toInteger() *2}", hasItems(40,050,60))
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
			
		;
	}
	
	//AULA21
	@Test
	public void devoFazerPesquisasAvancadasComXMLEJava() {
		ArrayList<NodeImpl> nomes = given()
		.when()
			.get("http://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(200)
			.extract().path("users.user.name.findAll{it.toString().contains('n')}")
		;
		System.out.println(nomes.toString());
		
		Assert.assertEquals(2, nomes.size());
		Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
		Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
	}
	
	//aula 22
	@Test
	public void devoFazerPesquisaComXpath() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(200)
			.body(hasXPath("count(/users/user)", is("3")))
			.body(hasXPath("/users/user[@id = '1']"))
			.body(hasXPath("//user[@id = '2']"))
			.body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))
			.body(hasXPath("//name[text() = 'Ana Julia']//following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))
			.body(hasXPath("/users/user/name", is ("João da Silva")))
			.body(hasXPath("//name", is("João da Silva")))
			.body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
			.body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
			.body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))
			.body(hasXPath("//user[age < 24]/name", is("Ana Julia")))
			.body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
			.body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina")))
			;
		
	}
	

}
