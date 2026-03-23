CREATE TABLE users (
  id_user SERIAL,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP,
  PRIMARY KEY (id_user)
);



CREATE TABLE roles (
  id_role SERIAL,
  role VARCHAR(50) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_role)
);


CREATE TABLE role_preferences (
  id_role_preferences SERIAL,
  fk_id_role INT NOT NULL,
  free_events INT NOT NULL,
  frequency VARCHAR(100) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_role_preferences),
  CONSTRAINT FK_role_preferences_id_role FOREIGN KEY (fk_id_role) REFERENCES roles(id_role)
);


CREATE TABLE user_roles (
  fk_id_user INT NOT NULL,
  fk_id_role INT NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (fk_id_user, fk_id_role),
  CONSTRAINT FK_user_roles_id_user FOREIGN KEY (fk_id_user) REFERENCES users(id_user),
  CONSTRAINT FK_user_roles_id_role FOREIGN KEY (fk_id_role) REFERENCES roles(id_role)
);


CREATE TABLE commission_rates (
  id_commission_rate SERIAL,
  payment_rate DECIMAL(5,2) NOT NULL,
  service_rate DECIMAL(5,2) NOT NULL,
  donation_rate DECIMAL(5,2) NOT NULL,
  PRIMARY KEY (id_commission_rate),
  CONSTRAINT valid_commission_donation CHECK (donation_rate >= 0 AND donation_rate <= 100),
  CONSTRAINT valid_commission_service CHECK (service_rate >= 0 AND service_rate <= 100),
  CONSTRAINT valid_commission_payment CHECK (payment_rate >= 0 AND payment_rate <= 100)
);


CREATE TABLE permissions (
  id_permissions SERIAL,
  fk_id_role INT NOT NULL,
  page VARCHAR(255) NOT NULL,
  list BOOLEAN DEFAULT FALSE,
  update BOOLEAN DEFAULT FALSE,
  detail BOOLEAN DEFAULT FALSE,
  delete BOOLEAN DEFAULT FALSE,
  is_active BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_permissions),
  CONSTRAINT FK_permissions_id_rol FOREIGN KEY (fk_id_role) REFERENCES roles(id_role)
);



CREATE TABLE game_types (
  id_game_type SERIAL,
  code VARCHAR(10) NOT NULL,
  name VARCHAR(250) NOT NULL,
  max_players INT,
  PRIMARY KEY (id_game_type)
);


CREATE TABLE categories (
  id_category SERIAL,
  code VARCHAR(10) NOT NULL,
  alias VARCHAR(50) NOT NULL,
  description VARCHAR(250),
  max_free_participants INT,
  PRIMARY KEY (id_category)
);


CREATE TABLE streamings_config (
  id_streamings_config SERIAL,
  name  VARCHAR(250) NOT NULL,
  base_url text NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  is_active BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_streamings_config)
);


CREATE TABLE tournaments (
  id_tournament SERIAL,
  fk_id_category INT NOT NULL,
  fk_id_game_type INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  description text,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  format VARCHAR(100) NOT NULL,
  is_free BOOLEAN NOT NULL,
  status VARCHAR(100) NOT NULL DEFAULT 'BORRADOR',
  max_tickets_participation INT NOT NULL,
  max_tickets_spectator INT NOT NULL,
  fk_id_creator INT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP,
  PRIMARY KEY (id_tournament),
  CONSTRAINT FK_tournaments_id_game_type FOREIGN KEY (fk_id_game_type) REFERENCES game_types(id_game_type),
  CONSTRAINT FK_tournaments_id_category FOREIGN KEY (fk_id_category) REFERENCES categories(id_category),
  CONSTRAINT FK_tournaments_id_creator FOREIGN KEY (fk_id_creator) REFERENCES users(id_user),
  CONSTRAINT valid_dates CHECK (end_date > start_date)
);


CREATE TABLE teams (
  id_team SERIAL,
  fk_id_tournament INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id_team),
  CONSTRAINT FK_teams_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament)
);


CREATE TABLE tournament_participants (
  id_tournament_participant SERIAL,
  fk_id_tournament INT NOT NULL,
  fk_id_user_player INT NOT NULL,
  fk_id_team INT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id_tournament_participant),
  CONSTRAINT FK_tournament_participants_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT FK_tournament_participants_id_user_player FOREIGN KEY (fk_id_user_player) REFERENCES users(id_user),
  CONSTRAINT FK_tournament_participants_id_team FOREIGN KEY (fk_id_team) REFERENCES teams(id_team)
);


CREATE TABLE groups (
  id_groups SERIAL,
  fk_id_tournament INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id_groups),
  CONSTRAINT FK_groups_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament)
);


CREATE TABLE group_participants (
  id_group_participants SERIAL,
  fk_id_group INT NOT NULL,
  fk_id_team INT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id_group_participants),
  CONSTRAINT FK_group_participants_id_team FOREIGN KEY (fk_id_team) REFERENCES teams(id_team),
  CONSTRAINT FK_group_participants_id_group FOREIGN KEY (fk_id_group) REFERENCES groups(id_groups)
);


CREATE TABLE team_matches (
  id_team_match SERIAL,
  fk_id_tournament INT NOT NULL,
  fk_id_group INT NOT NULL,
  round INT NOT NULL,
  fk_id_team_a INT NOT NULL,
  fk_id_team_b INT NOT NULL,
  score_a INT NOT NULL DEFAULT 0,
  score_b INT NOT NULL DEFAULT 0,
  status VARCHAR(100) NOT NULL,
  fk_winner INT,
  fk_user_create INT,
  fk_user_update INT,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP,
  PRIMARY KEY (id_team_match),
  CONSTRAINT FK_team_matches_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT FK_team_matches_id_team_b FOREIGN KEY (fk_id_team_b) REFERENCES teams(id_team),
  CONSTRAINT FK_team_matches_id_team_a FOREIGN KEY (fk_id_team_a) REFERENCES teams(id_team),
  CONSTRAINT FK_team_matches_id_group FOREIGN KEY (fk_id_group) REFERENCES groups(id_groups),
  CONSTRAINT FK_team_matches_winner FOREIGN KEY (fk_winner) REFERENCES teams(id_team),
  CONSTRAINT FK_team_matches_user_create FOREIGN KEY (fk_user_create) REFERENCES users(id_user),
  CONSTRAINT FK_team_matches_user_update FOREIGN KEY (fk_user_update) REFERENCES users(id_user)
);


CREATE TABLE individual_matches (
  id_individual_match SERIAL,
  fk_id_tournament INT NOT NULL,
  round INT NOT NULL,
  fk_participant_a INT NOT NULL,
  fk_participant_b INT NOT NULL,
  score_a INT NOT NULL,
  score_b INT NOT NULL,
  status VARCHAR(100) NOT NULL,
  fk_winner INT,
  fk_user_create INT,
  fk_user_update INT,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP,
  PRIMARY KEY (id_individual_match),
  CONSTRAINT FK_individual_matches_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT FK_individual_matches_participant_a FOREIGN KEY (fk_participant_a) REFERENCES tournament_participants(id_tournament_participant),
  CONSTRAINT FK_individual_matches_participant_b FOREIGN KEY (fk_participant_b) REFERENCES tournament_participants(id_tournament_participant),
  CONSTRAINT FK_individual_matches_winner FOREIGN KEY (fk_winner) REFERENCES tournament_participants(id_tournament_participant),
  CONSTRAINT FK_individual_matches_user_create FOREIGN KEY (fk_user_create) REFERENCES users(id_user),
  CONSTRAINT FK_individual_matches_user_update FOREIGN KEY (fk_user_update) REFERENCES users(id_user)
);
 


CREATE TABLE brackets (
  id SERIAL,
  fk_id_tournament INT NOT NULL,
  fk_id_team_matches INT NOT NULL,
  round INT NOT NULL,
  fk_id_next_match INT,
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id),
  CONSTRAINT FK_brackets_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT FK_brackets_id_team_matches FOREIGN KEY (fk_id_team_matches) REFERENCES team_matches(id_team_match),
  CONSTRAINT FK_brackets_id_next_match FOREIGN KEY (fk_id_next_match) REFERENCES team_matches(id_team_match)
);

CREATE TABLE standings (
  id_standing SERIAL,
  fk_id_tournament INT NOT NULL,
  fk_id_team INT NOT NULL,
  wins INT,
  losses INT,
  draws INT,
  points INT,
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id_standing),
  CONSTRAINT FK_standings_id_team FOREIGN KEY (fk_id_team) REFERENCES teams(id_team),
  CONSTRAINT FK_standings_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament)
);

CREATE TABLE battle_royale_rounds (
  id_battle_royale_round SERIAL,
  fk_id_tournament INT NOT NULL,
  round_number INT NOT NULL,
  eliminated_participant INT,
  fk_id_next_match INT,
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id_battle_royale_round),
  CONSTRAINT FK_battle_royale_rounds_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT FK_battle_royale_rounds_id_next_match FOREIGN KEY (fk_id_next_match) REFERENCES individual_matches(id_individual_match)
);



CREATE TABLE tournament_admins (
  id_tournament_admin SERIAL,
  fk_id_tournament INT NOT NULL,
  fk_id_user INT NOT NULL,
  role VARCHAR(100) NOT NULL,
  fk_user_create INT,
  fk_user_update INT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP,
  PRIMARY KEY (id_tournament_admin),
  CONSTRAINT FK_tournament_admins_id_user FOREIGN KEY (fk_id_user) REFERENCES users(id_user),
  CONSTRAINT FK_tournament_admins_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT FK_tournament_admins_user_create FOREIGN KEY (fk_user_create) REFERENCES users(id_user),
  CONSTRAINT FK_tournament_admins_user_update FOREIGN KEY (fk_user_update) REFERENCES users(id_user)
);


CREATE TABLE notifications (
  id_notification SERIAL,
  fk_id_tournament INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content text NOT NULL,
  recipients JSONB,
  is_sent BOOLEAN,
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id_notification),
  CONSTRAINT FK_notifications_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament)
);



CREATE TABLE tournament_stages  (
  id_tournament_stage SERIAL,
  fk_id_tournament INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  start_date date NOT NULL,
  end_date date NOT NULL,
  participant_price DECIMAL(10,2) NOT NULL,
  spectator_price DECIMAL(10,2) NOT NULL,
  max_participant_tickets INT NOT NULL,
  max_spectator_tickets INT NOT NULL,
  free_participant_slots INT NOT NULL,
  paid_participant_slots INT NOT NULL,
  free_spectator_slots INT NOT NULL,
  paid_spectator_slots INT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP,
  PRIMARY KEY (id_tournament_stage),
  CONSTRAINT FK_tournament_stages_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT valid_dates CHECK (end_date > start_date)
);


CREATE TABLE tournament_canceled (
  id_tournament_canceled SERIAL,
  fk_id_tournament INT NOT NULL,
  reasons VARCHAR(250) NOT NULL,
  PRIMARY KEY (id_tournament_canceled),
  CONSTRAINT FK_tournament_canceled_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament)
);

CREATE TABLE awards (
  id_award SERIAL,
  fk_id_tournament INT NOT NULL,
  description VARCHAR(100) NOT NULL,
  amount DECIMAL(10,2),
  PRIMARY KEY (id_award),
  CONSTRAINT FK_awards_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT valid_awards_amount CHECK (amount >= 0)
  
);

CREATE TABLE tournament_streams (
  id_tournament_stream SERIAL,
  fk_id_tournament INT NOT NULL,
  fk_id_streaming INT NOT NULL,
  stream_url text NOT NULL,
  access_type VARCHAR(100) NOT NULL,
  max_spectators INT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP,
  PRIMARY KEY (id_tournament_stream),
  CONSTRAINT FK_tournament_streams_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT FK_tournament_streams_id_streaming FOREIGN KEY (fk_id_streaming) REFERENCES streamings_config(id_streamings_config)
);

CREATE TABLE qr_codes (
  id_qr_code SERIAL,
  qr_data bytea,
  url_data VARCHAR(255),
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id_qr_code)
);

CREATE TABLE transactions (
  id_transaction SERIAL,
  fk_id_user INT NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  payment_method VARCHAR(100) NOT NULL,
  payment_status VARCHAR(100) NOT NULL,
  payment_details JSONB,
  created_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (id_transaction),
  CONSTRAINT FK_transactions_id_user FOREIGN KEY (fk_id_user) REFERENCES users(id_user),
  CONSTRAINT valid_transactions_total_amount CHECK (total_amount >= 0)
);


CREATE TABLE participant_tickets (
  id_participant_ticket SERIAL,
  fk_id_transaction INT NOT NULL,
  fk_id_participant INT NOT NULL,
  fk_id_stage  INT NOT NULL,
  fk_id_qr_code INT NOT NULL,
  ticket_type VARCHAR(100) NOT NULL,
  price_paid DECIMAL(10,2) NOT NULL,
  payment_comission DECIMAL(10,2) NOT NULL,
  sale_commission DECIMAL(10,2) NOT NULL,
  status VARCHAR(100) NOT NULL DEFAULT 'VIGENTE',
  PRIMARY KEY (id_participant_ticket),
  CONSTRAINT FK_participant_tickets_id_transaction FOREIGN KEY (fk_id_transaction) REFERENCES transactions(id_transaction),
  CONSTRAINT FK_participant_tickets_id_qr_code FOREIGN KEY (fk_id_qr_code) REFERENCES qr_codes(id_qr_code),
  CONSTRAINT FK_participant_tickets_id_participant FOREIGN KEY (fk_id_participant) REFERENCES tournament_participants(id_tournament_participant),
  CONSTRAINT FK_participant_tickets_id_stage  FOREIGN KEY (fk_id_stage ) REFERENCES tournament_stages (id_tournament_stage)
);


CREATE TABLE spectator_tickets (
  id_spectator_ticket SERIAL,
  fk_id_transaction INT NOT NULL,
  fk_id_tournament INT NOT NULL,
  fk_id_stage  INT NOT NULL,
  fk_id_qr_code INT NOT NULL,
  ticket_type VARCHAR(100) NOT NULL,
  stream_access BOOLEAN,
  price_paid DECIMAL(10,2) NOT NULL,
  payment_comission DECIMAL(10,2) NOT NULL,
  sale_commission DECIMAL(10,2) NOT NULL,
  status VARCHAR(100) NOT NULL DEFAULT 'VIGENTE',
  PRIMARY KEY (id_spectator_ticket),
  CONSTRAINT FK_spectator_tickets_id_qr_code FOREIGN KEY (fk_id_qr_code) REFERENCES qr_codes(id_qr_code),
  CONSTRAINT FK_spectator_tickets_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT FK_spectator_tickets_id_transaction FOREIGN KEY (fk_id_transaction) REFERENCES transactions(id_transaction),
  CONSTRAINT FK_spectator_tickets_id_stage  FOREIGN KEY (fk_id_stage ) REFERENCES tournament_stages (id_tournament_stage)
);


CREATE TABLE donations (
  id_donation SERIAL,
  fk_id_transactions INT NOT NULL,
  fk_id_tournament INT NOT NULL,
  fk_id_participants INT,
  amount DECIMAL(10,2),
  payment_comission DECIMAL(10,2) NOT NULL,
  sale_commission DECIMAL(10,2)  NOT NULL,
  PRIMARY KEY (id_donation),
  CONSTRAINT FK_donations_id_transactions FOREIGN KEY (fk_id_transactions) REFERENCES transactions(id_transaction),
  CONSTRAINT FK_donations_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
  CONSTRAINT FK_donations_id_participants FOREIGN KEY (fk_id_participants) REFERENCES tournament_participants(id_tournament_participant),
  CONSTRAINT valid_donations_amount CHECK (amount >= 0)
);


CREATE TABLE tickets_inventory (
    id_tickets_inventory SERIAL PRIMARY KEY,
    fk_id_tournament INT NOT NULL,
    fk_id_stage INT NOT NULL,
    ticket_type VARCHAR(20) NOT NULL, -- PARTICIPANTE -- ESPECTADOR
    total_quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    reserved_quantity INT NOT NULL,
    sold_quantity INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT FK_inventario_tickets_id_tournament FOREIGN KEY (fk_id_tournament) REFERENCES tournaments(id_tournament),
    CONSTRAINT FK_tickets_inventory_id_stage  FOREIGN KEY (fk_id_stage ) REFERENCES tournament_stages (id_tournament_stage)
);

insert into categories (code, alias, description, max_free_participants) values ('123','Competitivo Amateur','jugadores no profesionales que buscan competir',1);
insert into game_types (code, name, max_players) values ('456','Battle Royale',10);
insert into users (full_name, email, username, password) VALUES ('Juan Pérez', 'juan.perez@ejemplo.com', 'jperez', 'pass');
