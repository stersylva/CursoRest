package br.com.ster.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import io.restassured.http.ContentType;



public class VerbosTest {
	// aula 26
	@Test
	public void deveSalvarUsuario() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{\"name\": \"jose\",\"age\": 30}")
		.when()
		.post("http://restapi.wcaquino.me/users")
		.then()
		.log().all()
		.statusCode(201)
		.body("id", is(notNullValue()))
		.body("name", is("jose"))
		.body("age", is(50))
		;
	}

	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{\"age\": 30}")
		.when()
		.post("http://restapi.wcaquino.me/users")
		.then()
		.log().all()
		.statusCode(400)
		.body("id", is(nullValue()))
		.body("error", is("Name é um atributo obrigatório"))
		;	
	}
	//aula 27
	@Test
	public void deveSalvarUsuarioViaXML() {
		given()
		.log().all()
		.contentType(ContentType.XML)
		.body("<user><name>jose</name><age>30</age><salary>1234.5678</salary></user>")
		.when()
		.post("http://restapi.wcaquino.me/usersXML")
		.then()
		.log().all()
		.statusCode(201)
		.body("user.@id", is(notNullValue()))
		.body("user.name", is("jose"))
		.body("user.age", is(50))
		;
	}

	//alua 28
	@Test
	public void deveAlterarUsuario() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{\"name\": \"jose\",\"age\": 30}")
		.when()
		.put("http://restapi.wcaquino.me/users/1")
		.then()
		.log().all()
		.statusCode(200)
		.body("id", is(notNullValue()))
		.body("name", is("jose"))
		.body("age", is(30))
		.body("salary", is(1234.5678f))
		;
	}

	//alua 29
	@Test
	public void deveCustomizarURL() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{\"name\": \"jose\",\"age\": 50}")
		.when()
		.put("http://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")
		.then()
		.log().all()
		.statusCode(200)
		.body("id", is(notNullValue()))
		.body("name", is("jose"))
		.body("age", is(50))
		.body("salary", is(1234.5678f))
		;
	}

	@Test
	public void deveCustomizarURLParte2() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{\"name\": \"jose\",\"age\": 30}")
		.pathParam("entidade", "users")
		.pathParam("userId", 1)
		.when()
		.put("http://restapi.wcaquino.me/{entidade}/{userId}")
		.then()
		.log().all()
		.statusCode(200)
		.body("id", is(notNullValue()))
		.body("name", is("jose"))
		.body("age", is(30))
		.body("salary", is(1234.5678f))
		;
	}
	//aula 30
	@Test
	public void deveRemoverUsuario() {
		given()
		.log().all()
		.when()
		.delete("http://restapi.wcaquino.me/users/1")
		.then()
		.log().all()
		.statusCode(204)
		;
	}
	@Test
	public void naoDeveRemoverUsuarioInexixtente() {
		given()
		.log().all()
		.when()
		.delete("http://restapi.wcaquino.me/users/1000")
		.then()
		.log().all()
		.statusCode(400)
		.body("error", is("Registro inexistente"))
		;
	}
	// aula 32 baixa o gson no mavem pra rodar desse jeito
	@Test
	public void deveSalvarUsuarioUsandoMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Usuario via map");
		params.put("age", 25);

		given()
		.log().all()
		.contentType("application/json")
		.body(params)
		.when()
		.post("http://restapi.wcaquino.me/users")
		.then()
		.log().all()
		.statusCode(201)
		.body("id", is(notNullValue()))
		.body("name", is("Usuario via map"))
		.body("age", is(25))
		;
	}
	@Test
	public void deveSalvarUsuarioUsandoObjeto() {
		User user = new User("Usuario via objeto", 35);

		given()
		.log().all()
		.contentType("application/json")
		.body(user)
		.when()
		.post("http://restapi.wcaquino.me/users")
		.then()
		.log().all()
		.statusCode(201)
		.body("id", is(notNullValue()))
		.body("name", is("Usuario via objeto"))
		.body("age", is(35))
		;
	}

	// aula 33 importante
	@Test
	public void deveDeserializarObjetoSalvarUsuario() {
		User user = new User("Usuario deserializado", 35);

		User usurioInserido = given()
				.log().all()
				.contentType("application/json")
				.body(user)
				.when()
				.post("http://restapi.wcaquino.me/users")
				.then()
				.log().all()
				.statusCode(201)
				.extract().body().as(User.class)
				;
		System.out.println(usurioInserido);
		Assert.assertThat(usurioInserido.getId(), notNullValue());
		Assert.assertEquals("Usuario deserializado", usurioInserido.getName());
		Assert.assertThat(usurioInserido.getAge(), is(35));
	}

	// aula 34
	@Test
	public void deveSalvarUsuarioViaXMLUsandoObjeto() {
		User user = new User("Usuario XML", 40);
		given()
		.log().all()
		.contentType(ContentType.XML)
		.body(user)
		.when()
		.post("http://restapi.wcaquino.me/usersXML")
		.then()
		.log().all()
		.statusCode(201)
		.body("user.@id", is(notNullValue()))
		.body("user.name", is("Usuario XML"))
		.body("user.age", is("40"))
		;
	}

	// aula 35
	@Test
	public void deveserealizarXMLAoSalvarUsuario() {
		User user = new User("Usuario XML", 40);
		
		 User usurarioInserido = given()
		.log().all()
		.contentType(ContentType.XML)
		.body(user)
		.when()
		.post("http://restapi.wcaquino.me/usersXML")
		.then()
		.log().all()
		.statusCode(201)
		.extract().body().as(User.class)
		;
		Assert.assertThat(usurarioInserido.getId(), notNullValue());
		Assert.assertThat(usurarioInserido.getName(), is("Usuario XML"));
		Assert.assertThat(usurarioInserido.getAge(), is(40));
		Assert.assertThat(usurarioInserido.getSalary(), nullValue());
	}

}


