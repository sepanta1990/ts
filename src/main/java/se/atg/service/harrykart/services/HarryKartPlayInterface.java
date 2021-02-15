package se.atg.service.harrykart.services;

import se.atg.service.harrykart.model.HarryKart;
import se.atg.service.harrykart.model.RankingResponse;

public interface HarryKartPlayInterface {
    RankingResponse play(HarryKart harryKart) throws IllegalArgumentException;
}
