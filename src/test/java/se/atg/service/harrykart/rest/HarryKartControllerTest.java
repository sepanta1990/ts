package se.atg.service.harrykart.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import se.atg.service.harrykart.model.HarryKart;
import se.atg.service.harrykart.model.Ranking;
import se.atg.service.harrykart.model.RankingResponse;
import se.atg.service.harrykart.services.HarryKartPlayInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(HarryKartController.class)
public class HarryKartControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private HarryKartPlayInterface harryKartPlayService;

    @Autowired
    private MockMvc mockMvc;

    private String readFileToString(String filename) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        Objects.requireNonNull(in);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }


    @Test
    public void playHarryKarMustReturnOK() throws Exception {

        RankingResponse rankingResponse = new RankingResponse(Arrays.asList(new Ranking(1, "horse1"), new Ranking(2, "horse2")));

        when(harryKartPlayService.play(any(HarryKart.class))).thenReturn(rankingResponse);

        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("input_0.xml"))).
                andExpect(status().isOk()).
                andExpect(content().string(objectMapper.writeValueAsString(rankingResponse)));

        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("input_1.xml"))).
                andExpect(status().isOk()).
                andExpect(content().string(objectMapper.writeValueAsString(rankingResponse)));

        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("_AllWayTieTest.xml"))).
                andExpect(status().isOk()).
                andExpect(content().string(objectMapper.writeValueAsString(rankingResponse)));

        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("_InvalidHarryKartFormatTest.xml"))).
                andExpect(status().isOk()).
                andExpect(content().string(objectMapper.writeValueAsString(rankingResponse)));

        RankingResponse res2 = new RankingResponse(new ArrayList<>());
        when(harryKartPlayService.play(any(HarryKart.class))).thenReturn(res2);

        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("_InvalidHarryKartFormatTest.xml"))).
                andExpect(status().isOk()).
                andExpect(content().string(objectMapper.writeValueAsString(res2)));

        RankingResponse res3 = new RankingResponse(Collections.singletonList(new Ranking()));
        when(harryKartPlayService.play(any(HarryKart.class))).thenReturn(res3);
        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("_InvalidHarryKartFormatTest.xml"))).
                andExpect(status().isOk());


    }

    @Test
    public void playHarryKarMustReturnBadRequest() throws Exception {

        when(harryKartPlayService.play(any(HarryKart.class))).thenThrow(new IllegalArgumentException("Invalid number of powerUps for loops"));

        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("input_0.xml"))).
                andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("input_1.xml"))).
                andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("_AllWayTieTest.xml"))).
                andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("_InvalidHarryKartFormatTest.xml"))).
                andExpect(status().isBadRequest());


        this.mockMvc.perform(post("/api/play").contentType(MediaType.APPLICATION_XML)
                .content(readFileToString("_InvalidHarryKartFormatTest.xml"))).
                andExpect(status().isBadRequest());

    }
}