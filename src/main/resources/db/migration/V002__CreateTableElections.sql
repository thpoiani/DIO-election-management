CREATE TABLE elections (
  election_id VARCHAR(40) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (election_id));

CREATE TABLE election_candidates (
  election_id VARCHAR(40) NOT NULL,
  candidate_id VARCHAR(40) NOT NULL,
  votes INTEGER DEFAULT 0,
PRIMARY KEY (election_id, candidate_id));