package org.example.andensemesterproeve_thesemicolons.web;

import jakarta.servlet.http.HttpSession;
import org.example.andensemesterproeve_thesemicolons.application.TradingService;
import org.example.andensemesterproeve_thesemicolons.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trading")
public class TradingController {

    @Autowired
    private TradingService tradingService;

    @GetMapping("/myTradingCards")
    public String getMyTradingCardsPage(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("currentUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("tradingCards", tradingService.findUsersTradingCards(sessionUser));
        return "/trading/myTradingCards";
    }
}
