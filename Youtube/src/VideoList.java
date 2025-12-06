import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VideoList {
    private List<Video> videos = new ArrayList<>();
    private final int pageSize = 5;

    public void addVideo(Video v) {
        videos.add(v);
    }

    public Video getVideo(int idx) {
        if (idx >= 0 && idx < videos.size()) {
            return videos.get(idx);
        }
        return null;
    }

    public void removeVideo(User user, int idx) throws SQLException {
        if (idx >= 0 && idx < videos.size()) {
            videos.get(idx).deleteVideo(user);
            videos.remove(idx);
        }
    }

    public List<Video> getVideosForPage(int pageIndex) throws SQLException {
        List<Video> videos = new ArrayList<>();
        String query = """
                    SELECT
                        v.idVideo,
                        v.videoNama,
                        v.videoDurasi,
                        (
                            SELECT COUNT(*)
                            FROM Nonton n
                            WHERE n.idVideo = v.idVideo
                        ) AS videoViews,
                        v.videoDeskripsi,
                        v.videoSubtitle,
                        k.namaKanal,
                        k.idKanal,
                        v.videoPath,
                        v.statusVideo
                    FROM
                        Video v
                    INNER JOIN Kanal k ON v.idKanal = k.idKanal
                    WHERE v.statusVideo = 'A'
                    ORDER BY v.idVideo
                    OFFSET ? ROWS
                    FETCH NEXT ? ROWS ONLY;
                """;

        try (PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query)) {
            ps.setInt(1, pageIndex * pageSize);
            ps.setInt(2, pageSize);
            ResultSet rs = MainApp.konektor.getTable(ps);

            while (rs.next()) {
                Video video = new Video(
                rs.getInt(1),
                rs.getInt(8),
                rs.getString(2),
                rs.getInt(3),
                rs.getInt(4),
                rs.getString(5),
                rs.getString(6),
                rs.getString(7),
                rs.getString(9),
                rs.getString(10).charAt(0));
                videos.add(video);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videos;
    }

    public void showCurrentPage(int currentPageIndex) throws SQLException {
        System.out.println("=== VIDEO PAGE " + (currentPageIndex + 1) + " ===");
        videos = getVideosForPage(currentPageIndex);
        
        System.out.printf("%-5s %-30s %-10s %-10s %-10s\n", "NO", "TITLE", "DURATION", "VIEWS", "CHANNEL");
        for (int i = 0; i < videos.size(); i++) {
            System.out.printf("%-5d %s\n", currentPageIndex * pageSize + i + 1, videos.get(i));
        }
 
        System.out.println("=======================");
    }

    public void showAllPage() {
        System.out.println("=== VIDEO PAGE ALL ===");
        int start = 0;
        int end = videos.size();

        if (start == end) {
            System.out.println("There isn't any Video yet");
            System.out.println("==================");
            return;
        }

        System.out.printf("%-5s %-30s %-10s %-10s %-10s\n", "NO", "TITLE", "DURATION", "VIEWS", "CHANNEL");
        for (int i = start; i < end; i++) {
            System.out.printf("%-5d %s\n", i + 1, videos.get(i));
        }
       
        System.out.println("=======================");
    }

    public List<Video> getAllVideos() {
        return videos;
    }

    public void selectAndPlay(User user, int index, Scanner sc) throws SQLException {
        index--; // Karena user input dari 1
        if (index >= 0 && index < videos.size()) {
            videos.get(index).play(user, sc);
        } else {
            System.out.println("Video tidak ditemukan.");
        }
    }

    public void showDetails(int index, Scanner sc) throws SQLException {
        index--;
        if (index >= 0 && index < videos.size()) {
            videos.get(index).showDetails(sc);
        } else {
            System.out.println("Video tidak ditemukan.");
        }
    }

    public List<Video> search(String keyword) {
        List<Video> result = new ArrayList<>();
        for (Video video : videos) {
            if (video.getVideoNama().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(video);
            }
        }
        return result;
    }

    public int size() {
        return videos.size();
    }

}