import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChannelIndividu extends Channel{

    public ChannelIndividu(String channelName, String channelDescription) {
        super(channelName, channelDescription);
    }

    public ChannelIndividu(int idKanal, String channelName, String channelDescription, Date tanggalPembuatanKanal) {
        super(idKanal, channelName, channelDescription, tanggalPembuatanKanal);
    }

    @Override
    public void exportChannel() throws SQLException {
        String expQuery = """
                    INSERT INTO KanalIndividu (idKanal)
                    VALUES (?)
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(expQuery);
        ps.setInt(1, getIdKanal());
        MainApp.konektor.updateTable(ps);
    }
}
