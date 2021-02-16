package se.atg.service.harrykart.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.atg.service.harrykart.exception.ZeroOrNegativeSpeedException;
import se.atg.service.harrykart.model.HarryKart;
import se.atg.service.harrykart.model.Participant;
import se.atg.service.harrykart.model.Ranking;
import se.atg.service.harrykart.services.HarryKartPlayService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Ali Fathizadeh 2021-02-15
 * <p>
 * Service layer test
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class HarryKartPlayServiceTest {
    private final XmlMapper xmlMapper = new XmlMapper();
    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    private static final int LOOP_TIME_ROUNDING_SCALE = 10;

    @Autowired
    private HarryKartPlayService harryKartPlayService;

    private String readFileToString(String filename) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        Objects.requireNonNull(in);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }


    @Test
    public void lanesFinishInOrderTest() throws IOException {
        HarryKart hk = xmlMapper.readValue(readFileToString("input_0.xml"), HarryKart.class);

        List<Ranking> actualRanking = harryKartPlayService.play(hk).getRanking();
        List<Ranking> expectedRanking = Arrays.asList(new Ranking(1, "TIMETOBELUCKY"), new Ranking(2, "HERCULES BOKO"), new Ranking(3, "CARGO DOOR"));
        assertEquals(objectWriter.writeValueAsString(expectedRanking), objectWriter.writeValueAsString(actualRanking));
    }


    @Test
    public void twoWayTieTest() throws IOException {
        HarryKart hk = xmlMapper.readValue(readFileToString("_TwoWayTieTest.xml"), HarryKart.class);

        List<Ranking> actualRanking = harryKartPlayService.play(hk).getRanking();

        assertEquals(actualRanking.get(0).getPosition(), Integer.valueOf(1));
        assertEquals(actualRanking.get(0).getHorse(), "WAIKIKI SILVIO");

        assertEquals(actualRanking.get(1).getPosition(), Integer.valueOf(2));
        assertEquals(actualRanking.get(1).getHorse(), "HERCULES BOKO");
        String thirdPlace = actualRanking.get(2).getHorse();

        assertEquals(actualRanking.get(2).getPosition(), Integer.valueOf(3));
        assertEquals(actualRanking.get(3).getPosition(), Integer.valueOf(3));
        assertTrue(thirdPlace.equals("CARGO DOOR") || thirdPlace.equals("TIMETOBELUCKY"));

    }


    /**
     * All participants finish at the same time
     */
    @Test
    public void allWayTieTest() throws IOException {
        HarryKart harryKart = xmlMapper.readValue(readFileToString("_AllWayTieTest.xml"), HarryKart.class);

        List<Ranking> actualRanking = harryKartPlayService.play(harryKart).getRanking();
        actualRanking.forEach(ranking -> assertEquals(ranking.getPosition(), Integer.valueOf(1)));
    }


    @Test(expected = IllegalArgumentException.class)
    public void invalidXmlFormatTest() throws Exception {
        HarryKart harryKart = xmlMapper.readValue(readFileToString("_InvalidHarryKartFormatTest.xml"), HarryKart.class);

        harryKartPlayService.play(harryKart);
    }


    @Test
    public void zeroAndNegativePowerTest() throws IOException {
        HarryKart harryKart = xmlMapper.readValue(readFileToString("_ZeroAndNegativePowerTest.xml"), HarryKart.class);
//   should skip participants that stop in while of racing
        List<Ranking> actualRanking = harryKartPlayService.play(harryKart).getRanking();
        List<Ranking> expectedRanking = Arrays.asList(new Ranking(1, "WAIKIKI SILVIO"), new Ranking(2, "HERCULES BOKO"));
        assertEquals(objectWriter.writeValueAsString(expectedRanking), objectWriter.writeValueAsString(actualRanking));
    }

    @Test
    public void calculateTimeForParticipantTest() throws IOException {

        HarryKart harryKart = xmlMapper.readValue(readFileToString("input_1.xml"), HarryKart.class);
        BigDecimal resultTime = harryKartPlayService.calculateTimeForParticipant(new Participant(1, "TIMETOBELUCKY", 10), harryKart);
        BigDecimal expectedTime = BigDecimal.valueOf(250.00000).setScale(LOOP_TIME_ROUNDING_SCALE, RoundingMode.DOWN);
        assertEquals(resultTime, expectedTime);
    }


    @Test(expected = ZeroOrNegativeSpeedException.class)
    public void calculateTimeForParticipantZeroSpeedExceptionTest() throws IOException {
        HarryKart harryKart = xmlMapper.readValue(readFileToString("input_1.xml"), HarryKart.class);
//        participant with 0 baseSpeed
        harryKart.getParticipants().get(1).setBaseSpeed(0);
        harryKartPlayService.calculateTimeForParticipant(harryKart.getParticipants().get(1), harryKart);
    }


    @Test(expected = ZeroOrNegativeSpeedException.class)
    public void calculateTimeForParticipantNegativeSpeedExceptionTest() throws IOException {
        HarryKart harryKart = xmlMapper.readValue(readFileToString("input_1.xml"), HarryKart.class);
//        participant with negative baseSpeed
        harryKart.getParticipants().get(0).setBaseSpeed(-1);
        harryKartPlayService.calculateTimeForParticipant(harryKart.getParticipants().get(0), harryKart);

    }


    @Test
    public void calculateTimeForLaneTest() {
        List<Integer> powerUps = Arrays.asList(5, 10);
        BigDecimal resultTime = harryKartPlayService.calculateTimeForLane(3, 10, powerUps);
        BigDecimal expectedTime = BigDecimal.valueOf(206.6666666666).setScale(LOOP_TIME_ROUNDING_SCALE, RoundingMode.DOWN);
        assertEquals(resultTime, expectedTime);
    }


    @Test
    public void calculateTimeForLaneZeroPowerUpsLaneTest() {
        List<Integer> powerUps = Arrays.asList(0, 0);
        BigDecimal resultTime = harryKartPlayService.calculateTimeForLane(3, 10, powerUps);
        BigDecimal expectedTime = BigDecimal.valueOf(300.0000000000).setScale(LOOP_TIME_ROUNDING_SCALE, RoundingMode.DOWN);
        assertEquals(resultTime, expectedTime);
    }


    @Test(expected = IllegalArgumentException.class)
    public void calculateTimeForLaneInvalidPowerUpsNumberLaneTest() {
        List<Integer> powerUps = Arrays.asList(1, 2, 3);
//        3 powerUps for 3 loops is invalid
        harryKartPlayService.calculateTimeForLane(3, 10, powerUps);
    }

    @Test(expected = ZeroOrNegativeSpeedException.class)
    public void calculateTimeForLaneZeroSpeedInOnOfLoopsTest() {
        List<Integer> powerUps = Arrays.asList(5, -15);
//        throw exception for zero speed!
        harryKartPlayService.calculateTimeForLane(3, 10, powerUps);

    }

    @Test(expected = ZeroOrNegativeSpeedException.class)
    public void calculateTimeForLaneNegativeSpeedTest() {
        List<Integer> powerUps = Arrays.asList(5, -30);
//       throw exception for negative speed!
        harryKartPlayService.calculateTimeForLane(3, 10, powerUps);

    }


}