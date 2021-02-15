package se.atg.service.harrykart.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.atg.service.harrykart.exception.ZeroOrNegativeSpeedException;
import se.atg.service.harrykart.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * @author Ali Fathizadeh 2021-02-15
 *
 * Class to handle Harry Kart race result calculation and return
 */
@Service
public class HarryKartPlayService implements HarryKartPlayInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(HarryKartPlayService.class);
    private static final int LOOP_LENGTH = 1000;
    private static final int LOOP_TIME_ROUNDING_SCALE = 10;

    @Override
    public RankingResponse play(HarryKart harryKart) throws IllegalArgumentException {

        LOGGER.debug("play(), input: " + harryKart);
        /*
        Calculating match time for every participant
         */
        Map<String, BigDecimal> participantAndTimeMap = new HashMap<>();
        for (Participant participant : harryKart.getParticipants()) {
            try {
                participantAndTimeMap.put(participant.getName(), calculateTimeForParticipant(participant, harryKart));
            } catch (ZeroOrNegativeSpeedException e) {
                LOGGER.info(participant.getName() + " has stopped in the race and will be ignored.");
            }
        }

        /*
        Finding top 3 participant with lowest time
         */
        AtomicInteger rank = new AtomicInteger(1);
        AtomicReference<BigDecimal> time = new AtomicReference<>(BigDecimal.ZERO);

        final List<Ranking> topParticipantsRank = participantAndTimeMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(participantAndTime -> {

                    if (time.get().compareTo(BigDecimal.ZERO) == 0) {

                        time.set(participantAndTime.getValue());
                        return new Ranking(rank.get(), participantAndTime.getKey());
                    } else {
                        if (time.get().compareTo(participantAndTime.getValue()) == 0) {
                            /*
                            Participants have same rank. Set the rank
                             */
                            return new Ranking(rank.get(), participantAndTime.getKey());

                        } else {
                            /*
                            Participant have in different ranks. Set and increment rank
                             */
                            time.set(participantAndTime.getValue());
                            return new Ranking(rank.addAndGet(1), participantAndTime.getKey());
                        }
                    }
                }).filter(ranking -> rank.get() < 4).collect(Collectors.toList());

        RankingResponse rankingResponse = new RankingResponse(topParticipantsRank);
        LOGGER.info("play(), output: " + rankingResponse.toString());

        return rankingResponse;
    }


    public BigDecimal calculateTimeForParticipant(Participant participant, HarryKart harryKart) throws ZeroOrNegativeSpeedException {
        LOGGER.debug("calculateTimeForParticipant(), input: " + participant.toString() + " " + harryKart);

        /*
          Extracting power ups for the participant from request
         */
        List<Integer> participantPowerUps = new ArrayList<>();
        for (Loop loop : harryKart.getLoops()) {
            participantPowerUps.add(loop.getLanes().stream().filter(lane -> lane.getNumber() == participant.getLane()).findFirst().orElseThrow(() -> new IllegalArgumentException("Lane not found for the participant: " + participant + " in the loop: " + loop)).getPowerValue());
        }
        /*
          Calculating the time for the participant
         */
        BigDecimal calculatedTime = calculateTimeForLane(harryKart.getNumberOfLoops(), participant.getBaseSpeed(), participantPowerUps);
        LOGGER.info("calculateTimeForParticipant(), output: " + calculatedTime);
        return calculatedTime;
    }


    public BigDecimal calculateTimeForLane(int numberOfLoops, int baseSpeed, List<Integer> powerUps) throws IllegalArgumentException {
        LOGGER.info("calculateTimeForLane(), input: numberOfLoops = " + numberOfLoops + ", " + "baseSpeed = " + baseSpeed + ", powerUps = " + powerUps);

        if (numberOfLoops != powerUps.size() + 1) {
            throw new IllegalArgumentException("Invalid number of powerUps for loops. " + numberOfLoops + " loops must have " + (numberOfLoops - 1) + " powerUps.");
        }

        /*
          Calculating time for the first loop without power up
         */
        BigDecimal elapsedTime = calculateTimeForLoop(baseSpeed, LOOP_LENGTH);

        /*
          Calculating time for remaining loops with power ups
         */
        for (int powerUp : powerUps) {
            elapsedTime = elapsedTime.add(calculateTimeForLoop(baseSpeed += powerUp, LOOP_LENGTH));
        }

        LOGGER.info("calculateTimeForLane(), output: " + elapsedTime);
        return elapsedTime;
    }

    private BigDecimal calculateTimeForLoop(int speed, int loopLength) throws ZeroOrNegativeSpeedException {
        LOGGER.info("calculateTimeForLoop(), start, input: speed = " + speed + ", loopLength = " + loopLength + ", Decimal round scale: " + LOOP_TIME_ROUNDING_SCALE);

        if (speed <= 0) {
            throw new ZeroOrNegativeSpeedException("Negative or zero speed!");
        }
        BigDecimal timeForLoop = BigDecimal.valueOf(loopLength).divide(BigDecimal.valueOf(speed), LOOP_TIME_ROUNDING_SCALE, RoundingMode.DOWN);
        LOGGER.info("calculateTimeForLoop(), finished, output: " + timeForLoop);
        return timeForLoop;
    }

}
