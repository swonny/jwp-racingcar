package racingcar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import racingcar.controller.dto.GameRequestDtoForPlays;
import racingcar.controller.dto.RacingGameResultDto;
import racingcar.dao.RacingCarDao;
import racingcar.dao.RacingGameDao;
import racingcar.dao.WinnersDao;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class RacingCarServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private RacingCarService racingCarService;

    @BeforeEach
    public void setUp() {
        racingCarService = new RacingCarService(new RacingCarDao(jdbcTemplate), new RacingGameDao(jdbcTemplate), new WinnersDao(jdbcTemplate));
    }

    @Test
    @DisplayName("/plays 요청시 실행한 게임 결과 반환")
    void plays() {
        GameRequestDtoForPlays gameRequestDtoForPlays = new GameRequestDtoForPlays("메투,매투", "10");
        RacingGameResultDto racingGameResultDto = racingCarService.plays(gameRequestDtoForPlays);

        assertThat(racingGameResultDto.getWinners()).isNotNull();
        assertThat(racingGameResultDto.getRacingCars()).isNotNull();
        assertThat(racingGameResultDto.getCount()).isNotEqualTo(10);
    }

}
