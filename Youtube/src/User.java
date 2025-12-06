import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class User {
    private int idPengguna;
    protected String email;
    protected String namaPengguna;
    protected ArrayList<Channel> subscribedChannels;
    
    public User(String email, String namaPengguna) throws SQLException {
        this.email = email;
        this.namaPengguna = namaPengguna;
        subscribedChannels = importSubscribed();
    }
    
    public User(int idPengguna, String email, String namaPengguna) throws SQLException {
        this.idPengguna = idPengguna;
        this.email = email;
        this.namaPengguna = namaPengguna;
        subscribedChannels = importSubscribed();
    }

    public String getEmail() {
        return email;
    }

    public String getNamaPengguna() {
        return namaPengguna;
    }

    public ArrayList<Channel> getSubscribedChannels() {
        return subscribedChannels;
    }

    public void setIdPengguna(int id){
        this.idPengguna = id;
    }
    
    public int getIdPengguna(){
        return idPengguna;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        User u = (User) o;
        return idPengguna == u.idPengguna;
    }
    
    @Override
    public int hashCode() {
        return idPengguna;
    }

    private ArrayList<Channel> importSubscribed() throws SQLException {
        ArrayList<Channel> subs = new ArrayList<>();
        String query = """
                    SELECT 
                        k.idKanal,
                        k.namaKanal,
                        k.deskripsiKanal,
                        k.tanggalPembuatanKanal
                    FROM
                        (SELECT 
                            s.idKanal
                        FROM 
                            Subscribe s
                        WHERE 
                            s.idPengguna = ?) AS listSubs
                    INNER JOIN Kanal k ON listSubs.idKanal = k.idKanal
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idPengguna);    
        ResultSet rs = MainApp.konektor.getTable(ps);
        
        while (rs.next()) {
            subs.add(new ChannelIndividu(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4)));
        }
        return subs;
    }
    
    public void subscribe(int idKanal, User user) throws SQLException {
        if (checkSubscribed(idKanal, user)) {
            return;
        }
        
        String query = "INSERT INTO Subscribe (idPengguna, idKanal, tanggal_subscribe) VALUES (?, ?, ?)";
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idPengguna);
        ps.setInt(2, idKanal);
        ps.setDate(3, Date.valueOf(LocalDate.now()));
        MainApp.konektor.updateTable(ps);
        subscribedChannels.add(Channel.importChannel(idKanal));
        System.out.println("Subscribe successful");
    }

    public void unsubscribe(int idx, Channel channel) throws SQLException {
        subscribedChannels.remove(idx);
        channel.subscribers.remove(this);
        String query = """
                    DELETE FROM Subscribe
                    WHERE
                        idPengguna = ? AND
                        idKanal = ?
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, idPengguna);
        ps.setInt(2, channel.getIdKanal());
        MainApp.konektor.updateTable(ps);

        System.out.printf("#%s Removed from Suscribed\n", channel.namaKanal);
    }

    public boolean checkSubscribed(int idKanal, User user) throws SQLException{
        // Cek apakah user sudah subscribe channel ini
        String cekSubscribe = "SELECT idPengguna FROM Subscribe WHERE idPengguna = ? AND idKanal = ?";
        PreparedStatement checkPs = MainApp.konektor.getConnection().prepareStatement(cekSubscribe);
        checkPs.setInt(1, idPengguna);
        checkPs.setInt(2, idKanal);
        ResultSet rs = checkPs.executeQuery();

        if (rs.next()) {
            return true;
        }
        return false;
    }
    
    private static ResultSet importUser(String email, String passwordPengguna) throws SQLException {
        String query = """
                    SELECT 
                        Pengguna.idPengguna,
                        Pengguna.email,
                        Pengguna.namaPengguna
                    FROM 
                        Pengguna 
                    WHERE 
                        email = ? AND 
                        passwordPengguna = ?;
                """;
        
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, passwordPengguna);
        
        return MainApp.konektor.getTable(ps);
    }

    public static User login(String email, String passwordPengguna) throws SQLException {
        ResultSet rs = importUser(email, passwordPengguna);
        if (rs.next()) {
            return new User(rs.getInt(1), rs.getString(2), rs.getString(3));
        }
        return null;
    }

    public static User register(String email, String namaPengguna, String passwordPengguna) throws SQLException {
        if (checkRegister(email)) {
            return null;
        }

        String query = """
                    INSERT INTO Pengguna (
                        Pengguna.namaPengguna, 
                        Pengguna.passwordPengguna,
                        Pengguna.email,
                        Pengguna.tanggalPembuatanAkun,
                        Pengguna. tipePengguna
                    )
                    VALUES (?, ?, ?, ?, ?)
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, namaPengguna);
        ps.setString(2, passwordPengguna);
        ps.setString(3, email);
        ps.setDate(4, Date.valueOf(LocalDate.now()));
        ps.setInt(5, 1);
        MainApp.konektor.updateTable(ps);
        
        ResultSet rs = importUser(email, passwordPengguna);
        rs.next();
        
        return new User(rs.getInt(1), email, namaPengguna);
    }

    private static boolean checkRegister(String email) throws SQLException {
        String query = """
                    SELECT 
                        p.idPengguna
                    FROM 
                        Pengguna p
                    WHERE
                        p.email = ?
                """;

        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, email);

        ResultSet tempPS = MainApp.konektor.getTable(ps); 
        return tempPS.next();
    }
    // public List<Video> searchVideos(String keyword) {}

    // public void watchVideo(Video video) {}
    
    public static String getEmailbyID(int idPengguna) throws SQLException{
        String query = """
                            SELECT email
                            FROM Pengguna
                            WHERE idPengguna = ?
                            """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1,idPengguna);
        ResultSet rs = MainApp.konektor.getTable(ps);
        rs.next();
        return rs.getString(1);
    }

    public static int getIdbyEmail(String email) throws SQLException{
        String query = """
                            SELECT idPengguna
                            FROM Pengguna
                            WHERE email = ?
                            """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1,email);
        ResultSet rs = MainApp.konektor.getTable(ps);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }
}
