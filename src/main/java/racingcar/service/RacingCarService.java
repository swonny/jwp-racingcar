package racingcar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import racingcar.controller.dto.PlaysRequestDto;
import racingcar.controller.dto.PlaysResponseDto;
import racingcar.dao.RacingCarDao;
import racingcar.domain.Car;
import racingcar.domain.Cars;
import racingcar.domain.GameCount;
import racingcar.domain.PowerGenerator;
import racingcar.entity.RacingGameEntity;
import racingcar.util.CarNamesDivider;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Service
public class RacingCarService {

    private final RacingCarDao racingCarDao;

    @Autowired
    public RacingCarService(RacingCarDao racingCarDao) {
        this.racingCarDao = racingCarDao;
    }

    public List<PlaysResponseDto> query() {
        List<RacingGameEntity> racingGameEntities = racingCarDao.findAll(); // List<RacingGameEntity>
        return racingGameEntities.stream()
                .map(RacingGameEntity::toPlaysResponseDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public PlaysResponseDto plays(PlaysRequestDto playsRequestDto) {
        Cars cars = generateCars(playsRequestDto);
        GameCount gameCount = new GameCount(playsRequestDto.getCount());
        progress(cars, gameCount);
        String winners = winnersToString(cars);
        RacingGameEntity racingGameEntity = generateRacingGameEntity(playsRequestDto, cars, winners);
        racingCarDao.save(racingGameEntity);
        return racingGameEntity.toPlaysResponseDto();
    }

    private static RacingGameEntity generateRacingGameEntity(PlaysRequestDto playsRequestDto, Cars cars, String winners) {
        return new RacingGameEntity.Builder()
                .count(Integer.parseInt(playsRequestDto.getCount()))
                .winners(winners)
                .racingCars(cars.getCars())
                .build();
    }

    private static Cars generateCars(PlaysRequestDto playsRequestDto) {
        CarNamesDivider carNamesDivider = new CarNamesDivider();
        List<String> carNamesByDivider = carNamesDivider.divideCarNames(playsRequestDto.getNames());
        List<Car> inputCars = carNamesByDivider.stream()
                .map(Car::new)
                .collect(toList());
        return new Cars(inputCars);
    }

    private void progress(Cars cars, GameCount gameCount) {
        while (gameCount.isGameProgress()) {
            gameCount.proceedOnce();
            moveAllCar(cars);
        }
    }

    private void moveAllCar(Cars cars) {
        cars.moveAll(new PowerGenerator(new Random()));
    }

    private String winnersToString(Cars cars) {
        return cars.getWinners().stream()
                .map(Car::getName)
                .collect(joining(","));
    }

}
