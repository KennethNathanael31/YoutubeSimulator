USE Youtube

DROP TABLE IF EXISTS Dislike;
DROP TABLE IF EXISTS Liked;
DROP TABLE IF EXISTS Unggah;
DROP TABLE IF EXISTS Hapus;
DROP TABLE IF EXISTS Kelola;
DROP TABLE IF EXISTS Edit;
DROP TABLE IF EXISTS Diundang;
DROP TABLE IF EXISTS Subscribe;
DROP TABLE IF EXISTS Komen;
DROP TABLE IF EXISTS Nonton;
DROP TABLE IF EXISTS Video;
DROP TABLE IF EXISTS KanalGrup;
DROP TABLE IF EXISTS KanalIndividu;
DROP TABLE IF EXISTS Kanal;
DROP TABLE IF EXISTS Pengguna;

CREATE TABLE Pengguna (
    idPengguna INT IDENTITY (1, 1) PRIMARY KEY,
    namaPengguna VARCHAR(20) NOT NULL,
    passwordPengguna VARCHAR(16) NOT NULL,
    email VARCHAR(35) NOT NULL,
    tanggalPembuatanAkun DATE NOT NULL,
	tipePengguna INT NOT NULL
);

CREATE TABLE Kanal (
    idKanal INT IDENTITY (1, 1) PRIMARY KEY,
    idPengguna INT NOT NULL, -- Creator atau pemilik utama
    namaKanal VARCHAR(20) NOT NULL,
    deskripsiKanal VARCHAR(30) NOT NULL,
    tanggalPembuatanKanal DATE NOT NULL,
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna)
);

CREATE TABLE KanalIndividu (
    idKanal INT PRIMARY KEY NOT NULL,
    FOREIGN KEY (idKanal) REFERENCES Kanal(idKanal)
);

CREATE TABLE KanalGrup (
    idKanal INT PRIMARY KEY NOT NULL,
    jumlahMember INT NOT NULL,
    FOREIGN KEY (idKanal) REFERENCES Kanal(idKanal)
);

CREATE TABLE Video (
    idVideo INT IDENTITY (1, 1) PRIMARY KEY,
    videoNama VARCHAR(20) NOT NULL,
    videoDurasi INT NOT NULL,
    videoDeskripsi VARCHAR(75) NOT NULL,
    videoSubtitle VARCHAR(30) NOT NULL,
    statusVideo CHAR(1) NOT NULL,
    idKanal INT NOT NULL,
	videoPath VARCHAR(200) NOT NULL,
    FOREIGN KEY (idKanal) REFERENCES Kanal(idKanal)
);

CREATE TABLE Nonton (
    idPengguna INT NOT NULL,
    idVideo INT NOT NULL,
    tanggal_nonton DATETIME NOT NULL,
    PRIMARY KEY (idPengguna, idVideo),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna),
    FOREIGN KEY (idVideo) REFERENCES Video(idVideo)
);

CREATE TABLE Komen (
    idPengguna INT NOT NULL,
    idVideo INT NOT NULL,
    tanggal_komen DATETIME NOT NULL,
    isi_komen TEXT NOT NULL,
    PRIMARY KEY (idPengguna, idVideo, tanggal_komen),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna),
    FOREIGN KEY (idVideo) REFERENCES Video(idVideo)
);

CREATE TABLE Subscribe (
    idPengguna INT NOT NULL,
    idKanal INT NOT NULL,
    tanggal_subscribe DATE NOT NULL,
    PRIMARY KEY (idPengguna, idKanal),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna),
    FOREIGN KEY (idKanal) REFERENCES Kanal(idKanal)
);


CREATE TABLE Diundang (
    idKanal INT NOT NULL,
    idPengguna INT NOT NULL,
    tipeAkses INT NOT NULL,
    PRIMARY KEY (idKanal, idPengguna),
    FOREIGN KEY (idKanal) REFERENCES Kanal(idKanal),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna)
);

CREATE TABLE Edit (
    idVideo INT NOT NULL,
    idPengguna INT NOT NULL,
	idKanal INT NOT NULL,
    edit_tanggal DATE NOT NULL,
    edit_keterangan TEXT NOT NULL,
	FOREIGN KEY (idKanal) REFERENCES Kanal(idKanal),
    FOREIGN KEY (idVideo) REFERENCES Video(idVideo),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna)
);

CREATE TABLE Kelola (
    idKanal INT NOT NULL,
    idPengguna INT NOT NULL,
    kelola_tanggal DATE NOT NULL,
    kelola_keterangan TEXT NOT NULL,
    FOREIGN KEY (idKanal) REFERENCES Kanal(idKanal),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna)
);

CREATE TABLE Hapus (
    idVideo INT NOT NULL,
    idPengguna INT NOT NULL,
	idKanal INT NOT NULL,
    hapus_tanggal DATE NOT NULL,
	FOREIGN KEY (idKanal) REFERENCES Kanal(idKanal),
    FOREIGN KEY (idVideo) REFERENCES Video(idVideo),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna)
);

CREATE TABLE Unggah (
    idVideo INT NOT NULL PRIMARY KEY,
    idPengguna INT NOT NULL,
	idKanal INT NOT NULL,
    unggah_tanggal DATE NOT NULL,
    FOREIGN KEY (idVideo) REFERENCES Video(idVideo),
	FOREIGN KEY (idKanal) REFERENCES Kanal(idKanal),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna)
);

CREATE TABLE Liked (
    idPengguna INT NOT NULL,
    idVideo INT NOT NULL,
    tanggal_like DATE NOT NULL,
    PRIMARY KEY (idPengguna, idVideo),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna),
    FOREIGN KEY (idVideo) REFERENCES Video(idVideo)
);

CREATE TABLE Dislike (
    idPengguna INT NOT NULL,
    idVideo INT NOT NULL,
    tanggal_dislike DATE NOT NULL,
    PRIMARY KEY (idPengguna, idVideo),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna),
    FOREIGN KEY (idVideo) REFERENCES Video(idVideo)
);

-- Indexing
CREATE INDEX idx_pengguna_email ON Pengguna(email);

CREATE INDEX idx_kanal_idPengguna ON Kanal(idPengguna);

CREATE INDEX idx_video_idKanal ON Video(idKanal);

CREATE INDEX idx_unggah_pengguna ON Unggah(idPengguna);

CREATE INDEX idx_kelola_idKanal ON Kelola(idKanal);
CREATE INDEX idx_kelola_idPengguna ON Kelola(idPengguna);

CREATE INDEX idx_hapus_idVideo ON Hapus(idVideo);
CREATE INDEX idx_hapus_idPengguna ON Hapus(idPengguna);
CREATE INDEX idx_hapus_idKanal ON Hapus(idKanal);

CREATE INDEX idx_edit_idVideo ON Edit(idVideo);
CREATE INDEX idx_edit_idPengguna ON Edit(idPengguna);
CREATE INDEX idx_edit_idKanal ON Edit(idKanal);


--Data Dummy
INSERT INTO Pengguna (namaPengguna, passwordPengguna, email, tanggalPembuatanAkun, tipePengguna)
VALUES
('Dodo', 'pass123', 'dodo@email.com', '2024-01-15', 2), -- Pemilik
('Kapi', 'pass123', 'kapi@email.com', '2024-02-20', 2), -- Pemilik
('Wombat', 'pass123', 'wombat@email.com', '2024-03-05', 3),  -- Manajer
('Andrew', 'pass123', 'andrew@email.com', '2024-04-10', 3),   -- Editor
('Vandyka', 'pass123', 'vandyka@email.com', '2024-05-01', 3),   -- Editor Limited
('Kenneth', 'pass123', 'kenneth@email.com', '2024-05-10', 3), -- Subtitle Editor
('Bob', 'pass123', 'sentry@email.com', '2024-05-10', 3), -- Viewer
('Gru', 'pass123', 'minion@email.com', '2024-05-02', 1); -- Pengguna Biasa

INSERT INTO Kanal (idPengguna, namaKanal, deskripsiKanal, tanggalPembuatanKanal)
VALUES
(1, 'DodoAdventure', 'Petualangan seru Dodo', '2024-05-01'),  -- Dimiliki oleh Dodo (tipe: Pemilik)
(2, 'KapiPlay', 'Channel gaming kapi', '2024-05-02');    -- Dimiliki oleh Vandyka (tipe: Pemilik)

-- KanalIndividu: DodoAdventure (idKanal = 1)
INSERT INTO KanalIndividu (idKanal)
VALUES (1);

-- KanalGrup: KapiPlay (idKanal = 2), misalnya ada 0 anggota pada awalnya
INSERT INTO KanalGrup (idKanal, jumlahMember)
VALUES (2, 5);

-- Video untuk Kanal DodoAdventure (idKanal = 1)
INSERT INTO Video (videoNama, videoDurasi, videoDeskripsi, videoSubtitle, statusVideo, idKanal, videoPath)
VALUES 
('MisteriLabkom', 1200, 'Mencari Hata Rahasia Legendaris Labkom', 'SubIndo.srt', 'A', 1, 'https://youtu.be/5Y1GpL768Sk?si=LYojk-_DVy74LAES'),
('MenjelajahOBC', 1500, 'TierList Makanan OBC', 'SubEng.srt', 'A', 1, 'https://youtube.com/shorts/gO7h48jgO_w?si=QTP4ybcl93NG5oLP');

-- Video untuk Kanal KapiPlay (idKanal = 2)
INSERT INTO Video (videoNama, videoDurasi, videoDeskripsi, videoSubtitle, statusVideo, idKanal, videoPath)
VALUES 
('TournamentTetris', 1800, 'Free For All Tetris Match (I WIN!)', 'SubIndo.srt', 'A', 2, 'https://youtu.be/F9LFYXs55eQ?si=sXcGn4P9qYZLeZFG'),
('MortalWombat', 2000, 'By One Mortal Kombat bareng Dodo si Petualang', 'SubEng.srt', 'A', 2, 'https://youtu.be/hnI8sDJRLW8?si=BLu3yGrq2z7FOLOU');

-- Video untuk Kanal DodoAdventure (idKanal = 1)
INSERT INTO Video (videoNama, videoDurasi, videoDeskripsi, videoSubtitle, statusVideo, idKanal, videoPath)
VALUES 
('Xiao jie', 1200, 'Cute Xiao Jie Edit', 'SubIndo.srt', 'A', 1, 'https://youtube.com/shorts/j-JohPBnGkI?si=av-touumOqkdV_z7'),
('Cookies', 1500, 'Hectic Holic Cookie Sketsa', 'SubEng.srt', 'A', 1,'https://youtube.com/shorts/x92CurfLTiU?si=neAnsgbcKRTIzSRI'),
('Squidward Handsome', 1500, 'Spongebob - Handsome Squidward', 'SubEng.srt', 'A', 1,'https://youtu.be/LJYmX9Extck?si=q9ANh2E3eqvhFk63');

-- Video untuk Kanal KapiPlay (idKanal = 2)
INSERT INTO Video (videoNama, videoDurasi, videoDeskripsi, videoSubtitle, statusVideo, idKanal,videoPath)
VALUES 
('Repo', 1800, 'I Lovee', 'SubIndo.srt', 'A', 2,'https://youtube.com/shorts/ZkN0LdB4iAQ?si=8D-qSGJTT62napS9');

INSERT INTO Unggah (idVideo, idPengguna,idKanal, unggah_tanggal) 
VALUES
(1, 1, 1, '2024-05-01'),
(2, 1, 1, '2024-05-01'),
(3, 3, 2, '2024-05-02'),
(4, 4, 2, '2024-05-02'),
(5, 1, 1, '2024-05-03'),
(6, 1, 1, '2024-05-04'),
(7, 1, 1, '2024-05-05'),
(8, 5, 2, '2024-05-09');

--Notes :
	-- status video untuk soft delete
	-- A = Public
	-- B = Removed

INSERT INTO Nonton (idPengguna, idVideo, tanggal_nonton)
VALUES
(3, 1, '2024-05-15 10:30:00'),   -- Wombat nonton MisteriLabkom
(4, 2, '2024-05-16 13:00:00'),   -- Andrew nonton MenjelajahOBC
(5, 3, '2024-05-17 20:15:00'),   -- Vandyka nonton TournamentTetris
(6, 4, '2024-05-18 18:45:00');   -- Kenneth nonton MortalWombat

INSERT INTO Komen (idPengguna, idVideo, tanggal_komen, isi_komen)
VALUES
(3, 1, '2024-05-15 00:00:00', 'APA TUHHH!'),
(4, 2, '2024-05-16 00:00:00', 'Genji SOLO'),
(5, 3, '2024-05-17 00:00:00', 'Cheater Kapi'),
(6, 4, '2024-05-18 00:00:00', 'Dodo Skill Issue');

INSERT INTO Subscribe (idPengguna, idKanal, tanggal_subscribe)
VALUES
(3, 1, '2024-05-15'),  -- Wombat subscribe ke DodoAdventure
(4, 1, '2024-05-16'),  -- Andrew subscribe ke DodoAdventure
(5, 2, '2024-05-17'),  -- Vandyka subscribe ke KapiPlay
(6, 2, '2024-05-18');  -- Kenneth subscribe ke KapiPlay

INSERT INTO Diundang (idKanal, idPengguna, tipeAkses)
VALUES
(2, 2, 1),	-- Kapi termasuk Pemilik
(2, 3, 2),  -- Wombat diundang jadi Manajer di KapiPlay
(2, 4, 3),  -- Andrew diundang jadi Editor di KapiPlay
(2, 5, 4),  -- Vandyka diundang jadi Editor Limited di KapiPlay
(2, 6, 5),  -- Kenneth diundang jadi Subtitle Editor di KapiPlay
(2, 7, 6);  -- Bob diundang jadi Viewer di KapiPlay

INSERT INTO Edit (idVideo, idPengguna, idKanal, edit_tanggal, edit_keterangan) VALUES
(1, 1, 1, '2024-05-10', 'Edit Title'),
(3, 3, 2, '2024-05-11', 'Edit Title'),
(4, 4, 2, '2024-05-12', 'Edit Description'),
(1, 1, 2, '2024-05-13', 'Edit Subtitle'),
(4, 5 ,2, '2024-05-13', 'Edit Subtitle');

INSERT INTO Kelola (idKanal, idPengguna, kelola_tanggal, kelola_keterangan) VALUES
(1, 1, '2024-05-09', 'Update Channel Name'),
(1, 1, '2024-05-10', 'Update Channel Description'),
(2, 3, '2024-05-11', 'Update Channel Name'),
(2, 4, '2024-05-12', 'Update Channel Description');

INSERT INTO Liked (idPengguna, idVideo, tanggal_like) VALUES
(3, 1, '2024-05-15'),   
(4, 2, '2024-05-16'), 
(5, 3, '2024-05-17'),  
(6, 4, '2024-05-18');  

INSERT INTO DisLike (idPengguna, idVideo, tanggal_dislike) VALUES
(3, 2, '2024-05-15'),   
(4, 3, '2024-05-16'), 
(5, 4, '2024-05-17'),  
(6, 1, '2024-05-18'); 
