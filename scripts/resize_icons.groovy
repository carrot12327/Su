import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.RenderingHints
import java.awt.AlphaComposite

static BufferedImage padSquare(BufferedImage src) {
  int w = src.getWidth()
  int h = src.getHeight()
  int dim = Math.max(w, h)
  BufferedImage out = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB)
  def g = out.createGraphics()
  g.setComposite(AlphaComposite.Src)
  g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
  g.drawImage(src, (dim - w)/2 as int, (dim - h)/2 as int, null)
  g.dispose()
  return out
}

static BufferedImage scaleTo(BufferedImage src, int size) {
  BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
  def g = out.createGraphics()
  g.setComposite(AlphaComposite.Src)
  g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
  g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
  g.drawImage(src, 0, 0, size, size, null)
  g.dispose()
  return out
}

File logo = new File('/workspace/logo_download')
assert logo.isFile(): 'Logo file not found: ' + logo
BufferedImage base = ImageIO.read(logo)
assert base != null: 'Unable to read logo file'
base = padSquare(base)

Map<String,Integer> sizes = [mdpi:48, hdpi:72, xhdpi:96, xxhdpi:144, xxxhdpi:192]
File resRoot = new File('/workspace/app_pojavlauncher/src/main/res')

def names = ['ic_launcher','ic_launcher_foreground','ic_launcher_round']
sizes.each { dens, px ->
  File dir = new File(resRoot, 'mipmap-' + dens)
  dir.mkdirs()
  BufferedImage scaled = scaleTo(base, px as int)
  names.each { n ->
    File out = new File(dir, n + '.png')
    ImageIO.write(scaled, 'png', out)
    println 'Wrote ' + out + ' (' + px + 'x' + px + ')'
  }
}
// Play Store 512
BufferedImage ps = scaleTo(base, 512)
File psOut = new File('/workspace/app_pojavlauncher/src/main/ic_launcher-playstore.png')
ImageIO.write(ps, 'png', psOut)
println 'Wrote ' + psOut + ' (512x512)'