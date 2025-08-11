import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class IconResizer {
    private static BufferedImage padSquare(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int dim = Math.max(w, h);
        BufferedImage out = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        try {
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(src, (dim - w) / 2, (dim - h) / 2, null);
        } finally {
            g.dispose();
        }
        return out;
    }

    private static BufferedImage scaleTo(BufferedImage src, int size) {
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        try {
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(src, 0, 0, size, size, null);
        } finally {
            g.dispose();
        }
        return out;
    }

    private static void ensureDir(File dir) {
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create directory: " + dir);
        }
    }

    public static void main(String[] args) throws IOException {
        File logo = new File("/workspace/logo_download");
        if (!logo.isFile()) throw new IOException("Logo not found: " + logo.getAbsolutePath());
        BufferedImage base = ImageIO.read(logo);
        if (base == null) throw new IOException("Unable to read logo: " + logo.getAbsolutePath());
        base = padSquare(base);

        File resRoot = new File("/workspace/app_pojavlauncher/src/main/res");
        Map<String, Integer> sizes = new LinkedHashMap<>();
        sizes.put("mdpi", 48);
        sizes.put("hdpi", 72);
        sizes.put("xhdpi", 96);
        sizes.put("xxhdpi", 144);
        sizes.put("xxxhdpi", 192);

        String[] names = new String[]{"ic_launcher", "ic_launcher_foreground", "ic_launcher_round"};
        for (Map.Entry<String, Integer> e : sizes.entrySet()) {
            File dir = new File(resRoot, "mipmap-" + e.getKey());
            ensureDir(dir);
            BufferedImage scaled = scaleTo(base, e.getValue());
            for (String n : names) {
                File out = new File(dir, n + ".png");
                ImageIO.write(scaled, "png", out);
                System.out.println("Wrote " + out.getAbsolutePath() + " (" + e.getValue() + "x" + e.getValue() + ")");
            }
        }
        // Play Store icon 512x512
        BufferedImage ps = scaleTo(base, 512);
        File psOut = new File("/workspace/app_pojavlauncher/src/main/ic_launcher-playstore.png");
        ImageIO.write(ps, "png", psOut);
        System.out.println("Wrote " + psOut.getAbsolutePath() + " (512x512)");
    }
}