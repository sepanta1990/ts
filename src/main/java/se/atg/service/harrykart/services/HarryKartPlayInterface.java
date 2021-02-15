package se.atg.service.harrykart.services;

import se.atg.service.harrykart.model.HarryKart;
import se.atg.service.harrykart.model.RankingResponse;

/**
 * @author Ali Fathizadeh 2021-02-15
 */
public interface HarryKartPlayInterface {
    RankingResponse play(HarryKart harryKart) throws IllegalArgumentException;
}
