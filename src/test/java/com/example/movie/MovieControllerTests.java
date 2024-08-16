package com.example.movie;

import com.example.movie.model.Movie;
import com.example.movie.model.MovieRowMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = { "/schema.sql", "/data.sql" })
public class MovieControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        Movie movie1 = new Movie(1, "Avengers: Endgame", "Robert Downey Jr.");
        Movie movie2 = new Movie(2, "Avatar", "Sam Worthington");
        Movie movie3 = new Movie(3, "Titanic", "Leonardo DiCaprio");
        Movie movie4 = new Movie(4, "Star Wars: The Force Awakens", "Daisy Ridley");
        Movie movie5 = new Movie(5, "Jurassic World", "Chris Pratt");

        Movie moviePost = new Movie(6, "The Dark Knight", "Christian Bale");
        Movie moviePut = new Movie(2, "Avatar 2", "Sam Worthington");

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectWriter objectWriter = objectMapper.writer();

        @Test
        @Order(1)
        public void testGetMovies() throws Exception {
                mockMvc.perform(get("/movies"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", Matchers.hasSize(5)))
                                .andExpect(jsonPath("$[0].movieId", Matchers.equalTo(1)))
                                .andExpect(jsonPath("$[0].movieName",
                                                Matchers.equalToIgnoringCase(movie1.getMovieName())))
                                .andExpect(jsonPath("$[0].leadActor",
                                                Matchers.equalToIgnoringCase(movie1.getLeadActor())))
                                .andExpect(jsonPath("$[1].movieId", Matchers.equalTo(2)))
                                .andExpect(jsonPath("$[1].movieName",
                                                Matchers.equalToIgnoringCase(movie2.getMovieName())))
                                .andExpect(jsonPath("$[1].leadActor",
                                                Matchers.equalToIgnoringCase(movie2.getLeadActor())))
                                .andExpect(jsonPath("$[2].movieId", Matchers.equalTo(3)))
                                .andExpect(jsonPath("$[2].movieName",
                                                Matchers.equalToIgnoringCase(movie3.getMovieName())))
                                .andExpect(jsonPath("$[2].leadActor",
                                                Matchers.equalToIgnoringCase(movie3.getLeadActor())))
                                .andExpect(jsonPath("$[3].movieId", Matchers.equalTo(4)))
                                .andExpect(jsonPath("$[3].movieName",
                                                Matchers.equalToIgnoringCase(movie4.getMovieName())))
                                .andExpect(jsonPath("$[3].leadActor",
                                                Matchers.equalToIgnoringCase(movie4.getLeadActor())))
                                .andExpect(jsonPath("$[4].movieId", Matchers.equalTo(5)))
                                .andExpect(jsonPath("$[4].movieName",
                                                Matchers.equalToIgnoringCase(movie5.getMovieName())))
                                .andExpect(jsonPath("$[4].leadActor",
                                                Matchers.equalToIgnoringCase(movie5.getLeadActor())));
        }

        @Test
        @Order(2)
        public void testGetNotFound() throws Exception {
                mockMvc.perform(get("/movies/10"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @Order(3)
        public void testGetMovieById() throws Exception {
                mockMvc.perform(get("/movies/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.movieId", Matchers.equalTo(1)))
                                .andExpect(jsonPath("$.movieName", Matchers.equalToIgnoringCase(movie1.getMovieName())))
                                .andExpect(jsonPath("$.leadActor",
                                                Matchers.equalToIgnoringCase(movie1.getLeadActor())));

                mockMvc.perform(get("/movies/2"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.movieId", Matchers.equalTo(2)))
                                .andExpect(jsonPath("$.movieName", Matchers.equalToIgnoringCase(movie2.getMovieName())))
                                .andExpect(jsonPath("$.leadActor",
                                                Matchers.equalToIgnoringCase(movie2.getLeadActor())));

                mockMvc.perform(get("/movies/3"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.movieId", Matchers.equalTo(3)))
                                .andExpect(jsonPath("$.movieName", Matchers.equalToIgnoringCase(movie3.getMovieName())))
                                .andExpect(jsonPath("$.leadActor",
                                                Matchers.equalToIgnoringCase(movie3.getLeadActor())));

                mockMvc.perform(get("/movies/4"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.movieId", Matchers.equalTo(4)))
                                .andExpect(jsonPath("$.movieName", Matchers.equalToIgnoringCase(movie4.getMovieName())))
                                .andExpect(jsonPath("$.leadActor",
                                                Matchers.equalToIgnoringCase(movie4.getLeadActor())));

                mockMvc.perform(get("/movies/5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.movieId", Matchers.equalTo(5)))
                                .andExpect(jsonPath("$.movieName", Matchers.equalToIgnoringCase(movie5.getMovieName())))
                                .andExpect(jsonPath("$.leadActor",
                                                Matchers.equalToIgnoringCase(movie5.getLeadActor())));
        }

        @Test
        @Order(4)
        public void testPost() throws Exception {

                String content = objectWriter.writeValueAsString(moviePost);

                MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/movies")
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                                .content(content);
                mockMvc.perform(mockRequest)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.movieId", Matchers.equalTo(6)))
                                .andExpect(jsonPath("$.movieName", Matchers.equalToIgnoringCase("The Dark Knight")))
                                .andExpect(jsonPath("$.leadActor", Matchers.equalToIgnoringCase("Christian Bale")));
        }

        @Test
        @Order(5)
        public void testAfterPost() throws Exception {
                mockMvc.perform(get("/movies/6"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.movieId", Matchers.equalTo(6)))
                                .andExpect(jsonPath("$.movieName",
                                                Matchers.equalToIgnoringCase(moviePost.getMovieName())))
                                .andExpect(jsonPath("$.leadActor",
                                                Matchers.equalToIgnoringCase(moviePost.getLeadActor())));
        }

        @Test
        @Order(6)
        public void testDbAfterPost() throws Exception {

                Movie movie = jdbcTemplate.queryForObject("select * from movielist where movieid = ?",
                                new MovieRowMapper(), 6);
                assertEquals(movie.getMovieName(), moviePost.getMovieName());
                assertEquals(movie.getLeadActor(), moviePost.getLeadActor());

        }

        @Test
        @Order(7)
        public void testPutNotFound() throws Exception {
                Movie movie = new Movie(2, "Avatar 2", "Sam Worthington");
                String content = objectWriter.writeValueAsString(movie);

                MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/movies/33")
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                                .content(content);
                mockMvc.perform(mockRequest)
                                .andExpect(status().isNotFound());

        }

        @Test
        @Order(8)
        public void testPut() throws Exception {

                String content = objectWriter.writeValueAsString(moviePut);

                MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/movies/2")
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                                .content(content);
                mockMvc.perform(mockRequest)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.movieId", Matchers.equalTo(2)))
                                .andExpect(jsonPath("$.movieName", Matchers.equalToIgnoringCase("Avatar 2")))
                                .andExpect(jsonPath("$.leadActor", Matchers.equalToIgnoringCase("Sam Worthington")));
        }

        @Test
        @Order(9)
        public void testAfterPut() throws Exception {

                mockMvc.perform(get("/movies/2"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.movieId", Matchers.equalTo(2)))
                                .andExpect(jsonPath("$.movieName",
                                                Matchers.equalToIgnoringCase(moviePut.getMovieName())))
                                .andExpect(jsonPath("$.leadActor",
                                                Matchers.equalToIgnoringCase(moviePut.getLeadActor())));

        }

        @Test
        @Order(10)
        public void testDbAfterPut() throws Exception {

                Movie movie = jdbcTemplate.queryForObject("select * from movielist where movieid = ?",
                                new MovieRowMapper(), 2);

                assertEquals(moviePut.getMovieName(), movie.getMovieName());
                assertEquals(moviePut.getLeadActor(), movie.getLeadActor());
        }

        @Test
        @Order(11)
        public void testDeleteNotFound() throws Exception {

                MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/movies/90");
                mockMvc.perform(mockRequest).andExpect(status().isOk());

        }

        @Test
        @Order(12)
        public void testDelete() throws Exception {

                MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/movies/6");

                mockMvc.perform(mockRequest).andExpect(status().isOk());

        }

        @Test
        @Order(13)
        public void testAfterDelete() throws Exception {

                mockMvc.perform(get("/movies"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", Matchers.hasSize(5)))
                                .andExpect(jsonPath("$[0].movieId", Matchers.equalTo(1)))
                                .andExpect(jsonPath("$[0].movieName",
                                                Matchers.equalToIgnoringCase(movie1.getMovieName())))
                                .andExpect(jsonPath("$[0].leadActor",
                                                Matchers.equalToIgnoringCase(movie1.getLeadActor())))

                                .andExpect(jsonPath("$[1].movieId", Matchers.equalTo(2)))
                                .andExpect(jsonPath("$[1].movieName",
                                                Matchers.equalToIgnoringCase(moviePut.getMovieName())))
                                .andExpect(jsonPath("$[1].leadActor",
                                                Matchers.equalToIgnoringCase(moviePut.getLeadActor())))

                                .andExpect(jsonPath("$[2].movieId", Matchers.equalTo(3)))
                                .andExpect(jsonPath("$[2].movieName",
                                                Matchers.equalToIgnoringCase(movie3.getMovieName())))
                                .andExpect(jsonPath("$[2].leadActor",
                                                Matchers.equalToIgnoringCase(movie3.getLeadActor())))

                                .andExpect(jsonPath("$[3].movieId", Matchers.equalTo(4)))
                                .andExpect(jsonPath("$[3].movieName",
                                                Matchers.equalToIgnoringCase(movie4.getMovieName())))
                                .andExpect(jsonPath("$[3].leadActor",
                                                Matchers.equalToIgnoringCase(movie4.getLeadActor())))

                                .andExpect(jsonPath("$[4].movieId", Matchers.equalTo(5)))
                                .andExpect(jsonPath("$[4].movieName",
                                                Matchers.equalToIgnoringCase(movie5.getMovieName())))
                                .andExpect(jsonPath("$[4].leadActor",
                                                Matchers.equalToIgnoringCase(movie5.getLeadActor())));

                mockMvc.perform(get("/movies/6")).andExpect(status().isNotFound());
        }

        @AfterAll
        public void cleanup() {
                jdbcTemplate.execute("drop table movieList");
        }

}