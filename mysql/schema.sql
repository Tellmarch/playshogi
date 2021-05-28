SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema playshogi
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema playshogi
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `playshogi` ;
USE `playshogi` ;

-- -----------------------------------------------------
-- Table `playshogi`.`ps_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_user` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_user` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `password_hash` VARCHAR(45) NOT NULL,
  `email` VARCHAR(100) NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `administrator` TINYINT(1) NOT NULL DEFAULT 0,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `verified` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`username` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_kifu`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_kifu` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_kifu` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `author_id` INT NOT NULL,
  `usf` MEDIUMTEXT NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `type_id` TINYINT UNSIGNED NOT NULL,
  `starting_pos_id` INT UNSIGNED NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_player`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_player` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_player` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `jp_name` VARCHAR(45) NULL,
  `nickname` VARCHAR(45) NULL,
  `kishi` TINYINT(1) NOT NULL,
  `kishi_id` INT NULL,
  `kishi_rank` VARCHAR(5) NULL,
  `fesa_rank` VARCHAR(5) NULL,
  `81dojo_rank` VARCHAR(5) NULL,
  `sc24_rank` VARCHAR(5) NULL,
  `sw_rank` VARCHAR(5) NULL,
  `country` VARCHAR(5) NULL,
  `gender` CHAR(1) NULL,
  `birthdate` DATE NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_venue`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_venue` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_venue` (
  `id` INT UNSIGNED NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_game`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_game` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_game` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `kifu_id` INT UNSIGNED NOT NULL,
  `sente_id` INT UNSIGNED NULL,
  `gote_id` INT UNSIGNED NULL,
  `sente_name` VARCHAR(45) NULL,
  `gote_name` VARCHAR(45) NULL,
  `date_played` DATE NULL,
  `venue` INT UNSIGNED NOT NULL,
  `description` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_ps_game_1_idx` (`kifu_id` ASC),
  INDEX `fk_ps_game_2_idx` (`sente_id` ASC),
  INDEX `fk_ps_game_3_idx` (`gote_id` ASC),
  INDEX `fk_ps_game_4_idx` (`venue` ASC),
  CONSTRAINT `fk_ps_game_1`
    FOREIGN KEY (`kifu_id`)
    REFERENCES `playshogi`.`ps_kifu` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ps_game_2`
    FOREIGN KEY (`sente_id`)
    REFERENCES `playshogi`.`ps_player` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ps_game_3`
    FOREIGN KEY (`gote_id`)
    REFERENCES `playshogi`.`ps_player` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ps_game_4`
    FOREIGN KEY (`venue`)
    REFERENCES `playshogi`.`ps_venue` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_problem`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_problem` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_problem` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `kifu_id` INT UNSIGNED NOT NULL,
  `num_moves` INT UNSIGNED NULL,
  `elo` INT UNSIGNED NOT NULL DEFAULT 1000,
  `pb_type` TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_ps_problem_1_idx` (`kifu_id` ASC),
  CONSTRAINT `fk_ps_problem_1`
    FOREIGN KEY (`kifu_id`)
    REFERENCES `playshogi`.`ps_kifu` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_position`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_position` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_position` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `code_UNIQUE` (`code` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_kifupos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_kifupos` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_kifupos` (
  `kifu_id` INT UNSIGNED NOT NULL,
  `position_id` INT UNSIGNED NOT NULL,
  INDEX `fk_ps_kifupos_1_idx` (`kifu_id` ASC),
  INDEX `fk_ps_kifupos_2_idx` (`position_id` ASC),
  UNIQUE INDEX `kifupos_unique` (`kifu_id` ASC, `position_id` ASC),
  CONSTRAINT `fk_ps_kifupos_1`
    FOREIGN KEY (`kifu_id`)
    REFERENCES `playshogi`.`ps_kifu` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ps_kifupos_2`
    FOREIGN KEY (`position_id`)
    REFERENCES `playshogi`.`ps_position` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_gamesetmove`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_gamesetmove` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_gamesetmove` (
  `position_id` INT UNSIGNED NOT NULL,
  `move` CHAR(4) NOT NULL,
  `new_position_id` INT UNSIGNED NULL,
  `gameset_id` INT UNSIGNED NOT NULL,
  `num_total` INT UNSIGNED NOT NULL,
  INDEX `fk_ps_posmove_1_idx` (`position_id` ASC),
  INDEX `fk_ps_posmove_2_idx` (`new_position_id` ASC),
  UNIQUE INDEX `posmove_unique` (`position_id` ASC, `move` ASC, `gameset_id` ASC),
  CONSTRAINT `fk_ps_posmove_1`
    FOREIGN KEY (`position_id`)
    REFERENCES `playshogi`.`ps_position` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ps_posmove_2`
    FOREIGN KEY (`new_position_id`)
    REFERENCES `playshogi`.`ps_position` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_gameset`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_gameset` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_gameset` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(5000) NOT NULL,
  `visibility` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `owner_user_id` INT UNSIGNED NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_ps_gameset_1_idx` (`owner_user_id` ASC),
  CONSTRAINT `fk_ps_gameset_1`
    FOREIGN KEY (`owner_user_id`)
    REFERENCES `playshogi`.`ps_user` (`id`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_gamesetpos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_gamesetpos` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_gamesetpos` (
  `position_id` INT UNSIGNED NOT NULL,
  `gameset_id` INT UNSIGNED NOT NULL,
  `num_total` INT UNSIGNED NOT NULL,
  `num_sente_win` INT UNSIGNED NOT NULL,
  `num_gote_win` INT UNSIGNED NOT NULL,
  UNIQUE INDEX `gamesetpos_unique` (`position_id` ASC, `gameset_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_problemset`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_problemset` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_problemset` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_problemsetpbs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_problemsetpbs` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_problemsetpbs` (
  `problemset_id` INT UNSIGNED NOT NULL,
  `problem_id` INT UNSIGNED NOT NULL,
  INDEX `fk_pr_problemsetpbs_1_idx` (`problemset_id` ASC),
  INDEX `fk_pr_problemsetpbs_2_idx` (`problem_id` ASC),
  CONSTRAINT `fk_pr_problemsetpbs_1`
    FOREIGN KEY (`problemset_id`)
    REFERENCES `playshogi`.`ps_problemset` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pr_problemsetpbs_2`
    FOREIGN KEY (`problem_id`)
    REFERENCES `playshogi`.`ps_problem` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_userpbstats`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_userpbstats` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_userpbstats` (
  `user_id` INT UNSIGNED NOT NULL,
  `problem_id` INT UNSIGNED NOT NULL,
  `timestamp_attempted` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `time_spent_ms` INT NULL,
  `correct` TINYINT(1) NULL,
  INDEX `fk_pr_userpbstats_1_idx` (`user_id` ASC),
  INDEX `fk_pr_userpbstats_2_idx` (`problem_id` ASC),
  CONSTRAINT `fk_pr_userpbstats_1`
    FOREIGN KEY (`user_id`)
    REFERENCES `playshogi`.`ps_user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pr_userpbstats_2`
    FOREIGN KEY (`problem_id`)
    REFERENCES `playshogi`.`ps_problem` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_tag` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_tag` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_problemtag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_problemtag` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_problemtag` (
  `problem_id` INT UNSIGNED NOT NULL,
  `tag_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`problem_id`),
  INDEX `fk_ps_problemtag_2_idx` (`tag_id` ASC),
  CONSTRAINT `fk_ps_problemtag_1`
    FOREIGN KEY (`problem_id`)
    REFERENCES `playshogi`.`ps_problem` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ps_problemtag_2`
    FOREIGN KEY (`tag_id`)
    REFERENCES `playshogi`.`ps_tag` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_gamesetgame`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_gamesetgame` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_gamesetgame` (
  `gameset_id` INT UNSIGNED NOT NULL,
  `game_id` INT UNSIGNED NOT NULL,
  INDEX `fk_ps_gamesetgame_1_idx` (`gameset_id` ASC),
  INDEX `fk_ps_gamesetgame_2_idx` (`game_id` ASC),
  CONSTRAINT `fk_ps_gamesetgame_1`
    FOREIGN KEY (`gameset_id`)
    REFERENCES `playshogi`.`ps_gameset` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ps_gamesetgame_2`
    FOREIGN KEY (`game_id`)
    REFERENCES `playshogi`.`ps_game` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_highscore`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_highscore` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_highscore` (
  `index` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `score` INT NOT NULL,
  `timestamp_score` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` INT UNSIGNED NULL,
  `event` VARCHAR(45) NULL,
  PRIMARY KEY (`index`),
  INDEX `fk_ps_highscore_1_idx` (`user_id` ASC),
  CONSTRAINT `fk_ps_highscore_1`
    FOREIGN KEY (`user_id`)
    REFERENCES `playshogi`.`ps_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `playshogi`.`ps_lessons`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `playshogi`.`ps_lessons` ;

CREATE TABLE IF NOT EXISTS `playshogi`.`ps_lessons` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `kifu_id` INT UNSIGNED NULL,
  `parent_id` INT UNSIGNED NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` MEDIUMTEXT NULL,
  `tags` VARCHAR(1000) NULL,
  `preview_sfen` VARCHAR(200) NULL,
  `difficulty` TINYINT UNSIGNED NULL,
  `likes` INT NOT NULL DEFAULT 0,
  `author_id` INT UNSIGNED NULL,
  `hidden` TINYINT(1) NOT NULL DEFAULT 0,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `type` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `fk_ps_lessons_1_idx` (`kifu_id` ASC),
  INDEX `fk_ps_lessons_2_idx` (`author_id` ASC),
  CONSTRAINT `fk_ps_lessons_1`
    FOREIGN KEY (`kifu_id`)
    REFERENCES `playshogi`.`ps_kifu` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ps_lessons_2`
    FOREIGN KEY (`author_id`)
    REFERENCES `playshogi`.`ps_user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE = '';
DROP USER IF EXISTS playshogi;
SET SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
CREATE USER 'playshogi' IDENTIFIED BY 'playshogiDB1';

GRANT SELECT ON TABLE `playshogi`.* TO 'playshogi';
GRANT ALL ON `playshogi`.* TO 'playshogi';
GRANT SELECT, INSERT, TRIGGER ON TABLE `playshogi`.* TO 'playshogi';
GRANT SELECT, INSERT, TRIGGER, UPDATE, DELETE ON TABLE `playshogi`.* TO 'playshogi';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `playshogi`.`ps_venue`
-- -----------------------------------------------------
START TRANSACTION;
USE `playshogi`;
INSERT INTO `playshogi`.`ps_venue` (`id`, `name`) VALUES (1, 'Other');
INSERT INTO `playshogi`.`ps_venue` (`id`, `name`) VALUES (2, '81dojo');
INSERT INTO `playshogi`.`ps_venue` (`id`, `name`) VALUES (3, 'SC24');
INSERT INTO `playshogi`.`ps_venue` (`id`, `name`) VALUES (4, 'PlayOK');
INSERT INTO `playshogi`.`ps_venue` (`id`, `name`) VALUES (5, 'Amateur tournament');
INSERT INTO `playshogi`.`ps_venue` (`id`, `name`) VALUES (6, 'Pro tournament');

COMMIT;

