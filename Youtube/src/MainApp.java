import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MainApp {
    public static Controller controller = Controller.getInstance();
    public static ConnectDB konektor = ConnectDB.getInstance();
    public static boolean logOut = false;
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        int action;
        boolean checkAction = false;

        System.out.println("=== WELCOME TO YOUTUBE ===");
        while (true) {
            printCommand(new String[] { "Register", "Login", "Video Page", "Exit" });
            System.out.print("Pick your Action: ");
            action = sc.nextInt();
            System.out.println();
            switch (action) {
                case 1:
                    checkAction = registerPage(sc);
                    break;
                case 2:
                    checkAction = loginPage(sc);
                    break;
                case 3:
                    videoPage(sc);
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Command is not valid!");
                    break;
            }
            if (checkAction) {
                if (controller.importBrandchannel()) {
                    if (controller.importChannel()) {
                        homePageContentCreatorHaveChannel(sc);
                    }
                    else {
                        homePageContentCreatorNoChannel(sc);
                    }
                }
                else {
                    if (controller.importChannel()) {
                    homePageHaveChannel(sc);
                    }
                    else {
                        homePageNoChannel(sc);
                    }
                }
            }
        }
    }

    /*
     * Page untuk register bagi pengguna baru
     * Pengguna perlu memasukkan email, username, dan password
     * Email akan dicek di database untuk memastikan belum pernah terdaftar
     */
    public static boolean registerPage(Scanner sc) throws SQLException {
        System.out.println();
        String email, password, username;
        boolean check;

        sc.nextLine();
        System.out.println("==== REGISTER =====");
        System.out.print("Email: ");
        email = sc.nextLine();

        System.out.print("Username: ");
        username = sc.nextLine();

        System.out.print("Password: ");
        password = sc.nextLine();

        check = controller.register(email, username, password);
        if (!check) {
            System.out.println("Email already registered!\n");
        }
        else {
            System.out.println("Register success!\n");
        }
        return check;
    }

    /*
     * Page untuk login bagi pengguna yang sudah pernah mendaftar
     * Login memerlukan email dan password saja
     * Email dan password akan dikirim ke database untuk dicek
     */
    public static boolean loginPage(Scanner sc) throws SQLException {
        System.out.println();
        String email, password;
        boolean check;

        System.out.println("==== LOGIN =====");
        sc.nextLine();
        
        System.out.print("Email: ");
        email = sc.nextLine();

        System.out.print("Password: ");
        password = sc.nextLine();

        check = controller.login(email, password);
        
        if (!check) {
            System.out.println("Wrong email or password!\n");
        }
        else {
            System.out.println("Login successfull!\n");
        }
        return check;
    }

    /*
     * Home page bagi pengguna yang belum pernah membuat channel
     * Pengguna dapat membuat channel, menonton video, melihat subscriber, dan log out
     * Jika pengguna membuat channel, akan langsung dialihkan ke home page yang sudah memiliki channel
     */
    public static void homePageNoChannel(Scanner sc) throws SQLException{
        System.out.println();
        System.out.printf("=== WELCOME TO YOUTUBE %s ===\n", controller.getUser().getNamaPengguna());
        boolean check = false;

        while (true) {
            printCommand(new String[] { "Create Channel", "Video Page", "Subscription", "Log Out" });
            System.out.print("Pick your Action : ");
            int action = sc.nextInt();
            System.out.println();

            switch (action) {
                case 1:
                    check = makeChannelPage(sc);
                    break;
                case 2:
                    videoPage(sc);
                    break;
                case 3:
                    subscribePage(sc);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Command is not valid!");
                    break;
            }

            if (check) {
                homePageHaveChannel(sc);
                break;
            }
        }
    }

    /*
     * Page bagi pengguna yang ingin membuat channel
     * Pengguna perlu memasukkan nama, deskripsi, dan tipe channel yang akan dibuat
     */
    public static boolean makeChannelPage(Scanner sc) throws SQLException {
        System.out.println();
        String nama, deskripsi;
        int tipeChannel;

        System.out.print("Channel Name : ");
        sc.nextLine(); // tambahan biar kebaca
        nama = sc.nextLine();

        System.out.print("Channel Description : ");
        deskripsi = sc.nextLine();

        System.out.println("==== CHANNEL TYPE (Individual = 1 | Group = 2) ====");
        System.out.println("1. Individual");
        System.out.println("2. Group");
        System.out.print("Channel type (1 / 2): ");
        tipeChannel = sc.nextInt();

        return controller.makeChannel(nama, deskripsi, tipeChannel);
    }

    /*
     * Home page bagi pengguna yang sudah pernah membuat channel
     * Fitur sama seperti home page yang belum punya channel
     * Bedanya sekarang tombol buat channel menjadi my channel untuk melihat detail channel
     */
    public static void homePageHaveChannel(Scanner sc) throws SQLException{
        System.out.println();
        System.out.printf("=== WELCOME TO YOUTUBE %s ===\n", controller.getUser().getNamaPengguna());
        while (true) {
            printCommand(new String[] { "My Channel", "Video Page", "Subscription", "Log Out" });
            System.out.print("Pick your Action : ");
            int action = sc.nextInt();
            System.out.println();

            switch (action) {
                case 1:
                    if (controller.getChannel() instanceof ChannelGrup) {
                        myChannelGroupPage(sc);
                    } else {
                        myChannelPage(sc);
                    }
                    break;
                case 2:
                    videoPage(sc);
                    break;
                case 3:
                    subscribePage(sc);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Command is not valid!");
                    break;
            }
        }
    }
    
    public static void homePageContentCreatorNoChannel(Scanner sc) throws SQLException{
        System.out.println();
        System.out.printf("=== WELCOME TO YOUTUBE %s ===\n", controller.getUser().getNamaPengguna());
        boolean check = false;
        while (true) {
            printCommand(new String[] { "Make Channel", "Group Channel", "Video Page", "Subscription", "Log Out" });
            System.out.print("Pick your Action : ");
            int action = sc.nextInt();
            System.out.println();

            switch (action) {
                case 1:
                    check = makeChannelPage(sc);
                    break;
                case 2:
                    contentCreatorChannelPage(sc);
                    break;
                case 3:
                    videoPage(sc);
                    break;
                case 4:
                    subscribePage(sc);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Command is not valid!");
                    break;
            }
            if (check) {
                homePageContentCreatorHaveChannel(sc);
                break;
            }
        }
    }
    
    public static void homePageContentCreatorHaveChannel(Scanner sc) throws SQLException{
        System.out.println();
        System.out.printf("=== WELCOME TO YOUTUBE %s ===\n", controller.getUser().getNamaPengguna());
        while (true) {
            printCommand(new String[] { "My Channel", "Group Channel", "Video Page", "Subscription", "Log Out" });
            System.out.print("Pick your Action : ");
            int action = sc.nextInt();
            System.out.println();

            switch (action) {
                case 1:
                    if (controller.getChannel() instanceof ChannelGrup) {
                        myChannelGroupPage(sc);
                    } else {
                        myChannelPage(sc);
                    }
                    break;
                case 2:
                    contentCreatorChannelPage(sc);
                    break;
                case 3:
                    videoPage(sc);
                    break;
                case 4:
                    subscribePage(sc);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Command is not valid!");
                    break;
            }
        }
    }
    
    public static void contentCreatorChannelPage(Scanner sc) throws SQLException {
        System.out.println();
        while (true) {
            System.out.println("=== BRAND CHANNEL PAGE ===");
            printCommand(new String[] {"Upload Video", "See Uploaded Videos", "Remove Video", "Edit Video", "Channel Detail", 
                            "View Report", "Edit Member","Edit Channel", "Back"});
            System.out.print("Pick your action: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Video Title: ");
                    String title = sc.nextLine();

                    System.out.print("Video Description: ");
                    String desc = sc.nextLine();

                    System.out.print("Video Duration (in seconds): ");
                    int duration = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Video File URL (https://www.youtube.com/watch?v=Bwgnw0muVlY): ");
                    String videoPath = sc.nextLine();

                    System.out.print("Subtitle Text File Path (C:\\Users\\vandy\\Downloads\\subtitle.txt): ");
                    String subPath = sc.nextLine();
                    Video newVid = new Video(title, duration,  desc, subPath, videoPath, controller.getUser().getIdPengguna(),controller.getBrandChannel().getIdKanal());
                    controller.uploadVideoBrand(newVid);
                    break;

                case 2:
                    controller.printAllVideosBrand();
                    break;

                case 3:
                    controller.printAllVideosBrand();
                    System.out.print("Enter video index to remove: ");
                    int removeIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    controller.removeVideoBrand(removeIdx);
                    break;

                case 4:
                    controller.printAllVideosBrand();
                    System.out.print("Enter video index to edit: ");
                    int editIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    editVideoMenu(sc, editIdx, controller.getBrandChannel());
                    break;

                case 5:
                    controller.getBrandChannelDetail(sc);
                    break;

                case 6:
                    reportPage(sc, controller.getBrandChannel());
                    break;

                case 7:
                    editMemberMenu(sc, (ChannelGrup) controller.getBrandChannel());
                    break;
                case 8:
                    EditChannelPage(sc, controller.getBrandChannel());
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /*
     * Page untuk melihat list video yang pernah diupload
     * Page di load per 5 page sehingga ada tombol untuk next dan previous page
     * Pengguna dapat menonton video dan melihat detail video
     */
    public static void videoPage(Scanner sc) throws SQLException {
        System.out.println();
        int currentPage = 0;
        ResultSet rs = konektor.getTable("SELECT COUNT(idVideo) FROM Video");
        rs.next();
        int totalPage = (int) Math.ceil(rs.getInt(1) / 5.0);

        while (true) {
            controller.showCurrentPage(currentPage);
            printCommand(new String[] {"Next Page", "Previous Page", "Watch Video (by no.)", "Detail Video", 
                            "Exit to Home"});
            System.out.print("Pick your Action: ");
            int action = sc.nextInt();
            sc.nextLine();
            System.out.println();

            switch (action) {
                case 1:
                    if (currentPage + 1 == totalPage) {
                        System.out.println("=== Already on the last page! ===");
                        break;
                    }
                    currentPage++;
                    break;
                case 2:
                    if (currentPage == 0) {
                        System.out.println("=== Already on the first page! ===");
                        break;
                    }
                    currentPage--;
                    break;
                case 3:
                    System.out.print("Enter video index to watch: ");
                    int idx = sc.nextInt();
                    sc.nextLine();
                    controller.selectAndPlay(idx - currentPage * 5, sc);
                    break;
                case 4:
                    System.out.print("Enter index to see video detail: ");
                    int detailIdx = sc.nextInt();
                    sc.nextLine();
                    controller.showDetails(detailIdx - currentPage * 5, sc);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    /*
     * Page untuk melihat detail channel grup
     * Pengguna dapat mengupload, melihat upload, menghapus video, edit video, melihat 
     * detail channel, melihat laporan, dan mengatur member yang masuk ke dalam grup
     */
    public static void myChannelGroupPage(Scanner sc) throws SQLException {
        System.out.println();
        while (true) {
            System.out.println("=== MY CHANNEL PAGE ===");
            printCommand(new String[] {"Upload Video", "See Uploaded Videos", "Remove Video", "Edit Video", "Channel Detail", 
                            "View Report", "Edit Member","Edit Channel", "Back"});
            System.out.print("Pick your action: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Video Title: ");
                    String title = sc.nextLine();

                    System.out.print("Video Description: ");
                    String desc = sc.nextLine();

                    System.out.print("Video Duration (in seconds): ");
                    int duration = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Video File URL (https://www.youtube.com/watch?v=Bwgnw0muVlY): ");
                    String videoPath = sc.nextLine();

                    System.out.print("Subtitle Text File Path (C:\\Users\\vandy\\Downloads\\subtitle.txt): ");
                    String subPath = sc.nextLine();
                    Video newVid = new Video(title, duration,  desc, subPath, videoPath, controller.getUser().getIdPengguna(),controller.getChannel().getIdKanal());
                    controller.uploadVideo(newVid);
                    break;

                case 2:
                    controller.printAllVideos();
                    break;

                case 3:
                    controller.printAllVideos();
                    System.out.print("Enter video index to remove: ");
                    int removeIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    controller.removeVideo(removeIdx);
                    break;

                case 4:
                    controller.printAllVideos();
                    System.out.print("Enter video index to edit: ");
                    int editIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    editVideoMenu(sc, editIdx, controller.getChannel());
                    break;

                case 5:
                    controller.getChannelDetail(sc);
                    break;

                case 6:
                    reportPage(sc, controller.getChannel());
                    break;

                case 7:
                    editMemberMenu(sc, (ChannelGrup) controller.getChannel());
                    break;
                case 8:
                    EditChannelPage(sc, controller.getChannel());
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /*
     * Page untuk melihat detail channel individu
     * Pengguna dapat mengupload, melihat upload, menghapus video, edit video, melihat 
     * detail channel, dan melihat laporan
     */
    public static void myChannelPage(Scanner sc) throws SQLException {
        User user = controller.getUser();
        Channel myChannel = controller.getChannel();
        while (true) {
            System.out.println("==== MY CHANNEL PAGE ====");
            printCommand(new String[] {"Upload Video", "See Uploaded Videos", "Remove Video", "Edit Video", "Channel Detail", 
                            "View Report","Edit Channel", "Back"});
            System.out.print("Pick your action: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Video Title: ");
                    String title = sc.nextLine();

                    System.out.print("Video Description: ");
                    String desc = sc.nextLine();

                    System.out.print("Video Duration (in seconds): ");
                    int duration = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Video File URL (https://www.youtube.com/watch?v=Bwgnw0muVlY): ");
                    String videoPath = sc.nextLine();

                    System.out.print("Subtitle Text File Path (C:\\Users\\vandy\\Downloads\\subtitle.txt): ");
                    String subPath = sc.nextLine();

                    Video newVid = new Video(title, duration, desc, subPath, videoPath, user.getIdPengguna(),myChannel.getIdKanal());
                    controller.uploadVideo(newVid);
                    break;

                case 2:
                    controller.printAllVideos();
                    break;

                case 3:
                    controller.printAllVideos();
                    System.out.print("Enter video index to remove: ");
                    int removeIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    controller.removeVideo(removeIdx);
                    break;

                case 4:
                    controller.printAllVideos();
                    System.out.print("Enter video index to edit: ");
                    int editIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    editVideoMenu(sc, editIdx, controller.getChannel());
                    break;

                case 5:
                    controller.getChannelDetail(sc);
                    break;

                case 6:
                    reportPage(sc, controller.getChannel());
                    break;
                case 7:
                    EditChannelPage(sc, controller.getChannel());
                    break;
                case 8:
                    return; // Back to HomePage
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /*
     * Page untuk mengedit video
     * Pengguna dapat melihat detail video
     */
    public static void editVideoMenu(Scanner sc, int videoIndex, Channel myChannel) throws SQLException {
        System.out.println();
        if (videoIndex < 0 || videoIndex >= myChannel.uploaded.size()) {
            System.out.println("Video not found.\n");
            return;
        }
        Video video = myChannel.uploaded.getVideo(videoIndex);
        User user = controller.getUser();
        while (true) {
            System.out.println("=== EDIT VIDEO: " + video.getVideoNama() + " ===");
            printCommand(new String[] {"See Video Detail", "Edit title", "Edit Description", "Edit Subtitle", 
            "Play Video", "Back"}); 
            System.out.print("Pick your action: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    video.showDetails(sc);
                    break;
                case 2:
                    // System.out.print("New Title (blank = no change): ");
                    // String newTitle = sc.nextLine();
                    // if (!newTitle.isEmpty())
                    //     video.setVideoNama(newTitle);
                    // System.out.println("Title updated.");
                    if (myChannel instanceof ChannelIndividu) {
                        myChannel.changeVideoName(sc,video,user);
                    }else{
                        ChannelGrup temp = (ChannelGrup) myChannel;
                        temp.changeVideoName(sc,video,user);
                    }
                    break;
                case 3:
                    // System.out.print("New Description (blank = no change): ");
                    // String newDesc = sc.nextLine();
                    // if (!newDesc.isEmpty())
                    //     video.setVideoDeskripsi(newDesc);
                    // System.out.println("Description updated.");

                    if (myChannel instanceof ChannelIndividu) {
                        myChannel.changeVideoDescription(sc, video, user);
                    }else{
                        ChannelGrup temp = (ChannelGrup) myChannel;
                        temp.changeVideoDescription(sc, video, user);
                    }

                    break;
                case 4:
                    // System.out.print("Choose new subtitle file ((C:\\Users\\vandy\\Downloads\\subtitle.txt)): ");
                    // String subtitle = sc.next();
                    // video.setvideoSubtitle(subtitle);
                    // System.out.println("Subtitle added.");
                    if (myChannel instanceof ChannelIndividu) {
                        myChannel.changeVideoSubtitle(sc, video, user);
                    }else{
                        ChannelGrup temp = (ChannelGrup) myChannel;
                        temp.changeVideoSubtitle(sc, video, user);
                    }
                    break;
                case 5:
                    controller.play(sc, video);
                    break; 
                case 6:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    public static void editMemberMenu(Scanner sc, ChannelGrup tempGrup) throws SQLException {
        System.out.println();
        String roles[] = {"Manager", "Editor", "Editor Limited", "Subtitle Editor", "Viewer" };

        while (true) {
            tempGrup.printMember();
            System.out.println("\n==== EDIT MEMBER: " + tempGrup.namaKanal + " ====");
            System.out.println("1. Edit Member Role");
            System.out.println("2. Add Member");
            System.out.println("3. Remove Member");
            System.out.println("4. Back");

            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            System.out.println();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter member email to change role: ");
                    String changeEmail = sc.nextLine();
                    for (int i = 0; i < roles.length; i++) {
                        System.out.println((i+1) + ". " + roles[i]);
                    }
                    System.out.print("Enter role number: ");
                    int newRole = sc.nextInt();
                    sc.nextLine();

                    if (newRole < 1 || newRole > 5) {
                        System.out.println("Invalid role index.");
                        break;
                    }
                    
                    int check = tempGrup.changeRole(controller.getUser(), changeEmail, newRole+1);
                    if (check == 0) {
                        System.out.println("Role updated.");
                    } else if (check == 2) {
                        System.out.println("Member not found.");
                    }
                    break;

                case 2:
                    System.out.print("Add Member (email): ");
                    String email = sc.nextLine();
                    System.out.println("Choose role:");
                    for (int i = 0; i < roles.length; i++) {
                        System.out.println((i + 1) + ". " + roles[i]);
                    }
                    System.out.print("Enter role number: ");
                    int role = sc.nextInt();
                    sc.nextLine();

                    if (role < 1 || role > 5) {
                        System.out.println("Invalid role index.");
                        break;
                    }

                    int added = tempGrup.addMember(controller.getUser(), email, role + 1);
                    if (added == 0) {
                        System.out.println("Member added.");
                    } else if (added == 2) {
                        System.out.println("Member already exists.");
                    }
                    break;

                case 3:
                    System.out.print("Enter member email to remove: ");
                    String removeEmail = sc.nextLine();
                    int removed = tempGrup.removeMember(controller.getUser(), removeEmail);
                    if (removed == 0) {
                        System.out.println(removeEmail +" removed.");
                    } else if (removed == 2) {
                        System.out.println("Member not found.");
                    }
                    break;

                case 4:
                    return; // back to MyChannel

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    public static void subscribePage(Scanner sc) throws SQLException {
        User user = controller.getUser();
        System.out.printf("=== %s SUBSCRIPTION ===\n", user.namaPengguna);
        int channelIndex;

        while (true) {
            printCommand(new String[] { "Subscriber List", "Unsubscribe", "Channel Detail", "Back" });
            System.out.print("Pick your Action : ");
            int action = sc.nextInt();
            System.out.println();
            switch (action) {
                case 1:
                    System.out.printf("%-5s %-20s %-30s %-10s\n", "NO", "NAMA CHANNEL", "DESKRIPSI", "BANYAK SUBSCRIBER");
                    for (int i = 0; i < user.getSubscribedChannels().size(); i++) {
                        System.out.printf("%-5d %s\n", i + 1, user.getSubscribedChannels().get(i));
                    }
                    break;
                case 2:
                    System.out.print("Which Channel : ");
                    channelIndex = sc.nextInt() - 1;
                    sc.nextLine();
                    if (channelIndex < 0 || channelIndex >= user.getSubscribedChannels().size()) {
                        System.out.println("Channel index out of range!\n");
                        break;
                    }
                    user.unsubscribe(channelIndex, user.getSubscribedChannels().get(channelIndex));
                    break;
                case 3:
                    System.out.print("Which Channel: ");
                    channelIndex = sc.nextInt() - 1;
                    sc.nextLine();
                    if (channelIndex < 0 || channelIndex >= user.getSubscribedChannels().size()) {
                        System.out.println("Channel index out of range!\n");
                        break;
                    }
                    user.getSubscribedChannels().get(channelIndex).getChannelDetail(sc);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Command is not valid!\n");
                    break;
            }
            System.out.println();
        }
    }


    public static void EditChannelPage(Scanner sc, Channel myChannel) throws SQLException{
        User user = controller.getUser();
        //bisa 1 edit namaKanal
        //bisa 2 edit deskripsi kanal Page
        while (true) {
            printCommand(new String[] { "Edit Channel Name", "Edit Channel Description", "Back" });
            System.out.print("Pick your Action : ");
            int action = sc.nextInt();
            System.out.println();
            sc.nextLine();
             switch (action) {
                case 1:
                    if (myChannel instanceof ChannelIndividu) {
                        myChannel.changeName(sc, user);
                    }else{
                        ChannelGrup temp = (ChannelGrup) myChannel;
                        temp.changeName(sc, user);
                    }
                    
                    break;
                case 2:
                    if(myChannel instanceof ChannelIndividu){
                       myChannel.changeDescription(sc, user);
                    }else{
                        ChannelGrup temp = (ChannelGrup) myChannel;
                        temp.changeDescription(sc, user);
                    }
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Command is not valid!\n");
                    break;
            }
            System.out.println();
            
        }
    }

    public static void reportPage(Scanner sc, Channel myChannel) throws SQLException{
        User user = controller.getUser();
        //bisa 1 edit namaKanal
        //bisa 2 edit deskripsi kanal Page
        while (true) {
            printCommand(new String[] { "Channel Report", "Video Edit Report", "Video Delete Report", "Video Upload Report", "Channel edit report", "Back" });
            System.out.print("Pick your Action : ");
            int action = sc.nextInt();
            sc.nextLine();
            System.out.println();
             switch (action) {
                case 1:
                    myChannel.viewChannelReports(sc);
                    break;
                case 2:
                    myChannel.viewEditReport();
                    break;
                case 3:
                    myChannel.viewHapusReport();
                    break;
                case 4:
                    myChannel.viewUnggahReport();
                    break;
                case 5:
                    myChannel.viewKelolaReport();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Command is not valid!\n");
                    break;
            }
            System.out.println();
            
        }
    }

    public static void printCommand(String[] command) {
        System.out.println("==================");
        for (int i = 0; i < command.length; i++) {
            System.out.printf("[%d] %s\n", i + 1, command[i]);
        }
        System.out.println("==================");
    }

    public void getCurrentUser() {
        // return user;
    }
}
