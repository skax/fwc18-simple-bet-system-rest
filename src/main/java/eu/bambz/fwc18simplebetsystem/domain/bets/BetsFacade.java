package eu.bambz.fwc18simplebetsystem.domain.bets;

import eu.bambz.fwc18simplebetsystem.domain.bets.api.BetDto;
import eu.bambz.fwc18simplebetsystem.domain.bets.api.BetForm;
import eu.bambz.fwc18simplebetsystem.domain.bets.api.CannotBet;
import eu.bambz.fwc18simplebetsystem.domain.bets.query.BetsQueryFacade;
import eu.bambz.fwc18simplebetsystem.domain.common.AppError;
import eu.bambz.fwc18simplebetsystem.domain.common.TimeService;
import eu.bambz.fwc18simplebetsystem.domain.players.query.PlayersQueryFacade;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BetsFacade {

    private final BetsQueryFacade betsQueryFacade;
    private final BetsRepository betsRepository;
    private final TimeService timeService;
    private final PlayersQueryFacade playersQueryFacade;

    public Either<AppError, BetDto> bet(long id, BetForm betForm) {

        return betsRepository.load(id)
                .flatMap(bet -> update(bet, betForm))
                .flatMap(betsQueryFacade::find);

    }

    private Either<AppError, Long> update(Bet bet, BetForm betForm) {
        if(!bet.canUpdate(timeService.now()))
            return Either.left(new CannotBet());

        bet.update(betForm, playersQueryFacade.currentPlayer());
        betsRepository.update(bet);
        return Either.right(bet.getId());
    }


}
