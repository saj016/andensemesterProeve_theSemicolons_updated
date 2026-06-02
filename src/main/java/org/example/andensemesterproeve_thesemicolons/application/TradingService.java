package org.example.andensemesterproeve_thesemicolons.application;

import org.example.andensemesterproeve_thesemicolons.domain.Card;
import org.example.andensemesterproeve_thesemicolons.domain.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TradingService {

    public List<Card> findUsersTradingCards(User user) {
        List<Card> tradingCards = new ArrayList<>();
        for (int i = 0; i < user.getCards().size(); i++) {
            if (user.getCards().get(i).isForSwapping()) {
                tradingCards.add(user.getCards().get(i));
            }
        }
        return tradingCards;
    }
}
