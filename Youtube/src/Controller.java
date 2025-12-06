import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Controller {
    private User user;
    private Channel channel;
    private Channel brandChannel;
    private static VideoList publicList;
    private static Controller instance;

    private Controller() {
        publicList = new VideoList();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }
    
    public Channel getBrandChannel() {
        return brandChannel;
    }

    public VideoList getVideoList() {
        return publicList;
    }

    public boolean login(String email, String password) throws SQLException {
        user = User.login(email, password);
        return user != null;
    }

    public boolean register(String email, String username, String password) throws SQLException {
        user = User.register(email, username, password);
        return user != null;
    }

    public boolean importChannel() throws SQLException {
        channel = Channel.importChannel(user.getIdPengguna());
        if (channel != null) {
            channel.importSubscriber();
            channel.importVideo();
        }
        return channel != null;
    }
    
    public boolean importBrandchannel() throws SQLException {
        brandChannel = Channel.importChannelBrand(user.getIdPengguna());
        if (brandChannel != null) {
            brandChannel.importSubscriber();
            brandChannel.importVideo();
        }
        return brandChannel != null;
    }

    public boolean makeChannel(String nama, String deskripsi, int tipeChannel) throws SQLException {
        channel = Channel.makeChannel(user.getIdPengguna(), nama, deskripsi, tipeChannel);
        channel.import_ID_for_NewChannel(nama, deskripsi, tipeChannel);
        channel.exportChannel();
        
        return channel != null;
    }
    
    public void uploadVideo(Video video) throws SQLException {
        channel.uploadVideo(video);
        publicList.addVideo(video);
    }

    public void printAllVideos() {
        channel.printAllVideos();
    }

    public void removeVideo(int index) throws SQLException {
        channel.removeVideo(user, index);
    } 

    public void getChannelDetail(Scanner sc) throws SQLException {
        channel.getChannelDetail(sc);
    }

    public void viewChannelReports(Scanner sc) throws SQLException {
        channel.viewChannelReports(sc);
    }
    
    public void uploadVideoBrand(Video video) throws SQLException {
        brandChannel.uploadVideo(video);
        publicList.addVideo(video);
    }

    public void printAllVideosBrand() {
        brandChannel.printAllVideos();
    }

    public void removeVideoBrand(int index) throws SQLException {
        brandChannel.removeVideo(user, index);
    } 

    public void getBrandChannelDetail(Scanner sc) throws SQLException {
        brandChannel.getChannelDetail(sc);
    }

    public void viewBrandChannelReports(Scanner sc) throws SQLException {
        brandChannel.viewChannelReports(sc);
    }

    public void showCurrentPage(int currentPage) throws SQLException {
        publicList.showCurrentPage(currentPage);
    }

    public void selectAndPlay(int index, Scanner sc) throws SQLException {
        publicList.selectAndPlay(user, index, sc);
    }

    public void play(Scanner sc, Video video) throws SQLException {
        video.play(user, sc);
    }

    public void showDetails(int index, Scanner sc) throws SQLException {
        publicList.showDetails(index, sc);
    }
}
