import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("No target folder given!");
            return;
        }

        File targetFolder = new File(args[0]);
        if (!targetFolder.exists()) {
            System.out.println("Targetfolder not found: " + targetFolder.getAbsolutePath());
            return;
        }

        File originFolder = new File(args[1]);
        if (!originFolder.exists()) {
            System.out.println("Origin not found: " + originFolder.getAbsolutePath());
            return;
        }


        int amountOfBackups = 5;
        if (args.length > 2) {
            amountOfBackups = Integer.parseInt(args[2]);
        }

        archive(targetFolder, originFolder, amountOfBackups);
    }

    private static void archive(File targetFolder, File originFolder, int amountOfBackups) throws IOException {
        // Get All files in targetfolder

        List<File> backups = Arrays.stream(targetFolder.listFiles()).filter(of -> of.getName().startsWith("BACKUP_")).collect(Collectors.toList());
        backups.sort((o1, o2) -> o1.lastModified() < o2.lastModified() ? -1 : 1);


        File targetDirWithDate = new File(targetFolder, "BACKUP_" + LocalDate.now().toString().replace(".", "-"));
        if (!targetDirWithDate.exists()) {
            targetDirWithDate.mkdir();
        } else {
            System.out.println("Backup already existent: " + targetDirWithDate.getName());
            return;
        }


        FileUtils.copyDirectory(originFolder, targetDirWithDate);
        backups.add(targetDirWithDate);


        if (backups.size() > amountOfBackups) {
            int amountToDelete = backups.size() - amountOfBackups;
            for (int i = 0; i < amountToDelete; i++) {
                File backup = backups.get(i);
                FileUtils.deleteDirectory(backup);
            }
        }
        System.out.println("Done.");
    }
}