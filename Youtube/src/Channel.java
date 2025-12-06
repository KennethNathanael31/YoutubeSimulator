import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public abstract class Channel {
    private int idKanal;
    private int idPengguna;
    private Date tanggalPembuatanKanal;
    protected String namaKanal;
    protected String deskripsiKanal;
    protected VideoList uploaded = new VideoList();
    protected List<User> subscribers = new ArrayList<>();

    public Channel(String namaKanal, String deskripsiKanal) {
        this.namaKanal = namaKanal;
        this.deskripsiKanal = deskripsiKanal;
        this.tanggalPembuatanKanal = java.sql.Date.valueOf(LocalDate.now());
    }

    public Channel(int idKanal, String namaKanal, String deskripsiKanal, Date tanggal) {
        this.namaKanal = namaKanal;
        this.deskripsiKanal = deskripsiKanal;
        this.idKanal = idKanal;
        this.tanggalPembuatanKanal = tanggal;
    }

    public Date getTanggalPembuatanKanal() {
        return tanggalPembuatanKanal;
    }

    public void setNamaKanal(String namaKanal) throws SQLException {
        this.namaKanal = namaKanal;
        // Export to database
        String query = """
                    UPDATE Kanal
                    SET namaKanal = ?
                    WHERE idKanal = ?
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, getNamaKanal());
        ps.setInt(2, getIdKanal());
        MainApp.konektor.updateTable(ps);
    }

    public String getDeskripsiKanal() {
        return deskripsiKanal;
    }

    public int getIdKanal() {
        return idKanal;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void import_ID_for_NewChannel(String namaKanal, String deskripsiKanal, int tipeChannel) throws SQLException {
        String query = """
                    SELECT TOP 1 idKanal, idPengguna
                    FROM Kanal
                    ORDER BY idKanal DESC
                """;

        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ResultSet rs = MainApp.konektor.getTable(ps);
        if (rs.next()) {
            this.idKanal = rs.getInt("idKanal");
            this.idPengguna = rs.getInt("idPengguna");
        }

        if (tipeChannel == 2) {
            query = """
                    INSERT INTO Diundang (idKanal, idPengguna, tipeAkses)
                    VALUES(?, ?, ?)
                    """;
            ps = MainApp.konektor.getConnection().prepareStatement(query);
            ps.setInt(1, getIdKanal());
            ps.setInt(2, getIdPengguna());
            ps.setInt(3, 1);
            MainApp.konektor.updateTable(ps);
        }
    }

    public void setDeskripsiKanal(String deskripsiKanal) throws SQLException {
        this.deskripsiKanal = deskripsiKanal;
        String query = """
                    UPDATE Kanal
                    SET deskripsiKanal = ?
                    WHERE idKanal = ?
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, getDeskripsiKanal());
        ps.setInt(2, getIdKanal());
        MainApp.konektor.updateTable(ps);
    }

    public VideoList getUploaded() {
        return uploaded;
    }

    public void setUploaded(VideoList uploaded) {
        this.uploaded = uploaded;
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    public void setIdKanal(int idKanal) {
        this.idKanal = idKanal;
    }

    public void setTanggalPembuatanKanal(Date tanggalPembuatanKanal) {
        this.tanggalPembuatanKanal = tanggalPembuatanKanal;
    }

    public static Channel makeChannel(int idPengguna, String nama, String deskripsi, int tipeChannel)
            throws SQLException {
        String query = """
                INSERT INTO Kanal (idPengguna, namaKanal, deskripsiKanal, tanggalPembuatanKanal)
                VALUES(?, ?, ?, ?)
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idPengguna);
        ps.setString(2, nama);
        ps.setString(3, deskripsi);
        ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
        MainApp.konektor.updateTable(ps);

        // ResultSet rs = ps.getGeneratedKeys();
        // int idChannel = -1;
        // if (rs.next()) {
        // idChannel = rs.getInt(1);
        // }

        Channel channel;
        if (tipeChannel == 1) {
            channel = new ChannelIndividu(nama, deskripsi);
            updateTipeChannel(1, idPengguna);
        } else {
            channel = new ChannelGrup(nama, deskripsi);
            updateTipeChannel(2, idPengguna);
        }

        return channel;
    }

    private static void updateTipeChannel(int tipe, int idPengguna) throws SQLException {
        String query = """
                UPDATE Pengguna
                SET tipePengguna = ?
                WHERE idPengguna = ?
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, tipe);
        ps.setInt(2, idPengguna);

        MainApp.konektor.updateTable(ps);
    }

    public abstract void exportChannel() throws SQLException;

    @Override
    public String toString() {
        try {
            subscribers = importSubscriber();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String channel = String.format("%-20s %-30s %-10d", namaKanal, deskripsiKanal, subscribers.size());
        return channel;
    }

    public static Channel importChannel(int idPengguna) throws SQLException {
        Channel channel = null;

        String query = "SELECT * FROM Kanal WHERE idPengguna = ?";
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idPengguna);
        ResultSet rs = MainApp.konektor.getTable(ps);

        if (!rs.next()) {
            return null;
        }

        query = "SELECT idKanal FROM KanalGrup WHERE idKanal = ?";
        ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, rs.getInt("idKanal"));
        boolean checkGrup = MainApp.konektor.getTable(ps).next();

        if (checkGrup) {
            channel = new ChannelGrup(rs.getInt("idKanal"),
                    rs.getString("namaKanal"),
                    rs.getString("deskripsiKanal"),
                    rs.getDate("tanggalPembuatanKanal"));
        } else {
            channel = new ChannelIndividu(rs.getInt("idKanal"),
                    rs.getString("namaKanal"),
                    rs.getString("deskripsiKanal"),
                    rs.getDate("tanggalPembuatanKanal"));
        }
        return channel;
    }
    
    public static Channel importChannelBrand(int idPengguna) throws SQLException {
        Channel channel = null;
        String query;
        PreparedStatement ps;
        ResultSet rs = null;
        int idKanal = -1;
        
        query = "SELECT idKanal FROM Diundang WHERE idPengguna = ?";
        ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idPengguna);
        rs = MainApp.konektor.getTable(ps);
        if (rs.next()) {
            idKanal = rs.getInt("idKanal");
        }
        else {
            return null;
        }
        
        query = "SELECT * FROM Kanal WHERE idKanal = ?";
        ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idKanal);
        rs = MainApp.konektor.getTable(ps);
        rs.next();

        channel = new ChannelGrup(rs.getInt("idKanal"),
                rs.getString("namaKanal"),
                rs.getString("deskripsiKanal"),
                rs.getDate("tanggalPembuatanKanal"));
        return channel;
    }

    public List<User> importSubscriber() throws SQLException {
        subscribers = new ArrayList<>();
        // Import Subscribers
        String query = """
                    SELECT p.namaPengguna, p.email
                    FROM Subscribe s
                    JOIN Pengguna p ON s.idPengguna = p.idPengguna
                    WHERE s.idKanal = ?
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idKanal);
        ResultSet rs = MainApp.konektor.getTable(ps);

        while (rs.next()) {
            subscribers.add(new User(rs.getString(1), rs.getString(2)));
        }
        return subscribers;
    }

    public void importVideo() throws SQLException {
        uploaded = new VideoList();
        // Import Uploaded Videos
        String query = """
                    SELECT
                        v.idVideo,
                        v.videoNama,
                        v.videoDurasi,
                        (
                            SELECT COUNT(idPengguna)
                            FROM Nonton n
                            WHERE n.idVideo = v.idVideo
                        ) AS videoViews,
                        v.videoDeskripsi,
                        v.videoSubtitle,
                        v.statusVideo,
                        v.videoPath
                    FROM
                        (SELECT *
                        FROM Video
                        WHERE idKanal = ?) AS v
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idKanal);
        ResultSet rs = MainApp.konektor.getTable(ps);

        String nama, deskripsi, subtitle, path, status;
        int idV, durasi, view;
        while (rs.next()) {
            idV = rs.getInt(1);
            nama = rs.getString(2);
            durasi = rs.getInt(3);
            view = rs.getInt(4);
            deskripsi = rs.getString(5);
            subtitle = rs.getString(6);
            status = rs.getString(7);
            path = rs.getString(8);
            this.uploaded.addVideo(new Video(idV, idKanal, nama, durasi, view, deskripsi, subtitle, namaKanal, path,
                    status.charAt(0)));

        }
    }

    public void uploadVideo(Video video) throws SQLException {
        // export ke database
        video.setMaker(getNamaKanal());
        video.exportDBVideo();
        // tambah ke uploaded
        this.uploaded.addVideo(video);
    }

    public String getNamaKanal() {
        return namaKanal;
    }

    public List<User> getSubscribers() throws SQLException {
        importSubscriber();
        return subscribers;
    }

    public void printAllVideos() {
        uploaded.showAllPage();
    }

    public void removeVideo(User user, int idx) throws SQLException {
        uploaded.removeVideo(user, idx);
    }

    public void getChannelDetail(Scanner sc) throws SQLException {
        importSubscriber();
        importVideo();
        String action = "";
        while (true) {
            System.out.println("==== CHANNEL DETAIL ====");
            System.out.println("Channel Name : " + namaKanal);
            System.out.println("Description : " + deskripsiKanal);
            System.out.println("Created Date : " + tanggalPembuatanKanal);
            System.out.println("Subscribers Count : " + subscribers.size());
            System.out.println("Total Videos : " + uploaded.size());
            System.out.println("========================");
            System.out.println("Done ? (Y/y)");
            System.out.print("Answer : ");
            action = sc.next();
            if (action.toUpperCase().equals("Y")) {
                return;
            }
        }
    }

    public void changeName(Scanner sc, User user) throws SQLException {
        while (true) {
            System.out.println("===== CHANNEL EDIT =====");
            System.out.println("Current Name : " + getNamaKanal());
            System.out.print("Change Channel Name? (Y/N): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("Y")) {
                System.out.print("New Channel Name: ");
                String newName = sc.nextLine().trim();
                setNamaKanal(newName);
                updateKelola("Update Channel Name", user);

                System.out.println("Channel name updated successfully.");
                return;
            } else {
                return;
            }
        }
    }

    public void updateKelola(String keterangan, User user) throws SQLException {
        String query = """
                    INSERT INTO Kelola (idKanal, idPengguna, kelola_tanggal, kelola_keterangan)
                    VALUES (?, ?, ?, ?)
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);

        int idKanal = getIdKanal();
        int idPengguna = user.getIdPengguna();
        java.sql.Date tanggal = new java.sql.Date(System.currentTimeMillis());
        ps.setInt(1, idKanal);
        ps.setInt(2, idPengguna);
        ps.setDate(3, tanggal);
        ps.setString(4, keterangan);

        MainApp.konektor.updateTable(ps);
    }

    public void updateEdit(String keterangan, User user, int idVideo) throws SQLException {
        // update ke Edit
        String query = """
                    Insert into Edit(idVideo, idPengguna, idKanal, edit_tanggal, edit_keterangan) Values
                    (?, ?, ?, ?, ?)
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idVideo);
        ps.setInt(2, user.getIdPengguna());
        ps.setInt(3, getIdKanal());
        ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
        ps.setString(5, keterangan);
        MainApp.konektor.updateTable(ps);
    }

    public void changeDescription(Scanner sc, User user) throws SQLException {
        while (true) {
            System.out.println("===== CHANNEL EDIT =====");
            System.out.print("Change Channel Description? (Y/N): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("Y")) {
                System.out.print("New Description: ");
                String newDesc = sc.nextLine().trim();
                setDeskripsiKanal(newDesc);
                updateKelola("Update Channel Description", user);
                System.out.println("Channel description updated successfully.");
            }
            return;
        }
    }

    public void changeVideoName(Scanner sc, Video video, User user) throws SQLException {
        System.out.println("Current Video Name : " + video.getVideoNama());
        System.out.print("New Name : ");
        String newName = sc.nextLine();
        System.out.print("Proceed? (Y/N): ");
        String confirm = sc.nextLine();
        if (confirm.equalsIgnoreCase("Y")) {
            video.setVideoNama(newName, user);
            updateEdit("Edit Title", user, video.getIdVideo());
            System.out.println("Video name updated.");
        } else {
            System.out.println("Update canceled.");
        }
    }

    public void changeVideoDescription(Scanner sc, Video video, User user) throws SQLException {
        System.out.println("Current Description: " + video.getVideoDeskripsi());
        System.out.print("New Description: ");
        String newDesc = sc.nextLine();
        System.out.print("Proceed? (Y/N): ");
        String confirm = sc.nextLine();
        if (confirm.equalsIgnoreCase("Y")) {
            video.setVideoDescription(newDesc, user);
            updateEdit("Edit Description", user, video.getIdVideo());
            System.out.println("Video description updated.");
        } else {
            System.out.println("Update canceled.");
        }
    }

    public void changeVideoSubtitle(Scanner sc, Video video, User user) throws SQLException {
        System.out.println("Current Subtitle Path: " + video.getvideoSubtitle());
        System.out.print("New subtitle file ((C:\\Users\\vandy\\Downloads\\subtitle.txt)):  ");
        String newSubtitle = sc.nextLine();
        System.out.print("Proceed? (Y/N): ");
        String confirm = sc.nextLine();
        if (confirm.equalsIgnoreCase("Y")) {
            video.setVideoSubtitle(newSubtitle, user);
            updateEdit("Edit Subtitle", user, video.getIdVideo());
            System.out.println("Video subtitle updated.");
        } else {
            System.out.println("Update canceled.");
        }
    }

    public void viewKelolaReport() throws SQLException {
        String query = """
                    SELECT
                        p.namaPengguna,
                        R1.kelola_tanggal,
                        R1.kelola_keterangan
                    FROM
                        (SELECT
                            e.idPengguna,
                            e.kelola_tanggal,
                            e.kelola_keterangan
                        FROM
                            Kelola e
                        WHERE
                            e.idkanal = ?) AS R1
                    INNER JOIN Pengguna p ON R1.idPengguna = p.idPengguna
                """;

        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idKanal);
        ResultSet rs = MainApp.konektor.getTable(ps);

        System.out.printf("%-5s %-30s %-15s %-30s\n", "NO", "MEMBER", "DATE", "DESCRIPTION");
        int idx = 1;
        while (rs.next()) {
            String namaPengguna = rs.getString("namaPengguna");
            Date tanggalEdit = rs.getDate("kelola_tanggal");
            String keterangan = rs.getString("kelola_keterangan");

            System.out.printf("%-5d %-30s %-15s %-30s\n", idx++, namaPengguna, tanggalEdit,
                    keterangan);
        }
    }

    public void viewHapusReport() throws SQLException {
        String query = """
                    SELECT
                        v.videoNama,
                        p.namaPengguna,
                        R1.hapus_tanggal
                    FROM
                        (SELECT
                            h.idPengguna,
                            h.idVideo,
                            h.hapus_tanggal
                        FROM
                            Hapus h
                        WHERE
                            h.idkanal = ?) AS R1
                    JOIN Pengguna p ON R1.idPengguna = p.idPengguna
                    JOIN Video v ON R1.idVideo = v.idVideo
                """;

        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idKanal);
        ResultSet rs = MainApp.konektor.getTable(ps);
        System.out.println("======= LAPORAN HAPUS VIDEO =======");
        System.out.printf("%-5s %-30s %-30s %-15s\n", "NO", "TITLE", "MEMBER", "DATE");
        int idx = 1;
        while (rs.next()) {
            String namaVideo = rs.getString("videoNama");
            String namaPengguna = rs.getString("namaPengguna");
            Date tanggalHapus = rs.getDate("hapus_tanggal");
            System.out.printf("%-5d %-30s %-30s %-15s\n", idx, namaVideo, namaPengguna, tanggalHapus);
            idx++;
        }
    }

    public void viewEditReport() throws SQLException {
        String query = """
                    SELECT
                        v.videoNama,
                        p.namaPengguna,
                        R1.edit_tanggal,
                        R1.edit_keterangan
                    FROM
                        (SELECT
                            e.idPengguna,
                            e.idVideo,
                            e.edit_tanggal,
                            e.edit_keterangan
                        FROM
                            Edit e
                        WHERE
                            e.idKanal = ?) as R1
                    INNER JOIN Pengguna p ON R1.idPengguna = p.idPengguna
                    INNER JOIN Video v ON R1.idVideo = v.idVideo
                    ORDER BY R1.edit_tanggal DESC
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idKanal);
        ResultSet rs = MainApp.konektor.getTable(ps);
        int idx = 1;

        System.out.println("======= LAPORAN EDIT VIDEO =======");

        System.out.printf("%-5s %-30s %-10s %-15s %-30s\n", "NO", "TITLE", "MEMBER", "DATE", "DESCRIPTION");
        while (rs.next()) {
            String namaVideo = rs.getString("videoNama");
            String namaPengguna = rs.getString("namaPengguna");
            Date tanggalEdit = rs.getDate("edit_tanggal");
            String keterangan = rs.getString("edit_keterangan");
            System.out.printf("%-5d %-30s %-10s %-15s %-30s\n", idx++, namaVideo, namaPengguna, tanggalEdit.toString(),
                    keterangan);
        }
    }

    public void viewUnggahReport() throws SQLException {
        String query = """
                    SELECT
                        v.videoNama,
                        p.namaPengguna,
                        R1.unggah_tanggal
                    FROM
                        (SELECT
                            u.idPengguna,
                            u.idVideo,
                            u.unggah_tanggal
                        FROM
                            Unggah u
                        WHERE
                            u.idKanal = ?) as R1
                    INNER JOIN Pengguna p ON R1.idPengguna = p.idPengguna
                    INNER JOIN Video v ON R1.idVideo = v.idVideo
                    ORDER BY R1.unggah_tanggal Desc
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idKanal);
        ResultSet rs = MainApp.konektor.getTable(ps);
        int idx = 1;
        System.out.println("======= LAPORAN UNGGAHAN VIDEO =======");
        System.out.printf("%-5s %-30s %-30s %-15s\n", "NO", "TITLE", "MEMBER", "DATE");
        while (rs.next()) {
            String namaVideo = rs.getString("videoNama");
            String namaPengguna = rs.getString("namaPengguna");
            Date tanggalUnggah = rs.getDate("unggah_tanggal");
            System.out.printf("%-5d %-30s %-30s %-15s\n", idx, namaVideo, namaPengguna, tanggalUnggah);
            idx++;
        }
    }

    public void viewChannelReports(Scanner sc) throws SQLException {
        int totalLikes = 0;
        int totalDislikes = 0;
        int totalComments = 0;
        int totalViews = 0;
        for (Video video : uploaded.getAllVideos()) {
            video.importDBVideo();
            totalLikes += video.getLikes().size();
            totalDislikes += video.getDislikes().size();
            totalComments += video.commentList.size();
            totalViews += video.getViews();
        }
        String action = "";
        while (true) {
            System.out.println("===== CHANNEL REPORT =====");
            System.out.println("Channel Name       : " + namaKanal);
            System.out.println("Total Videos       : " + uploaded.size());
            System.out.println("Total Likes        : " + totalLikes);
            System.out.println("Total Dislikes     : " + totalDislikes);
            System.out.println("Total Comments     : " + totalComments);
            System.out.println("Total Views        : " + totalViews);
            System.out.println("================================");
            System.out.println("Done ? (Y/y)");
            System.out.print("Answer : ");
            action = sc.next();
            if (action.toUpperCase().equals("Y")) {
                return;
            }
        }
    }

    public void addSubscriber(User user) {
        subscribers.add(user);
    }
}