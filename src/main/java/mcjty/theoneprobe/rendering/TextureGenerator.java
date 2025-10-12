package mcjty.theoneprobe.rendering;

import mcjty.theoneprobe.setup.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextureGenerator {

    public enum PatternType {
        CHECKERBOARD, NOISE, OUTLINE, SOLID, GRADIENT, GUI_BACKGROUND, GUI_BUTTON
    }

    public static ResourceLocation generateTexture(String name, int width, int height, PatternType pattern, Color color1, Color color2, int patternSize) {
        CommonProxy.getLogger().info("Creating Texture: {}", name);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor;

                switch (pattern) {
                    case CHECKERBOARD:
                        pixelColor = ((x / patternSize + y / patternSize) % 2 == 0) ? color1 : color2;
                        break;

                    case NOISE:
                        double noiseValue = Math.random();

                        if (noiseValue < 0.2) {
                            pixelColor = darken(color1, 0.3f);
                        } else if (noiseValue < 0.4) {
                            pixelColor = darken(color1, 0.15f);
                        } else if (noiseValue < 0.6) {
                            pixelColor = color1;
                        } else if (noiseValue < 0.8) {
                            pixelColor = brighten(color1, 0.15f);
                        } else {
                            pixelColor = brighten(color1, 0.3f);
                        }
                        break;

                    case OUTLINE:
                        int outlineThickness = patternSize > 0 ? patternSize : 1;

                        boolean isBorder = x < outlineThickness || y < outlineThickness ||
                                x >= width - outlineThickness || y >= height - outlineThickness;

                        pixelColor = isBorder ? color1 : new Color(0, 0, 0, 0); // Transparent fill
                        break;

                    case GRADIENT:
                        float ratio = (float) y / height;
                        int r = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
                        int g = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
                        int b = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
                        int a = (int) (color1.getAlpha() * (1 - ratio) + color2.getAlpha() * ratio);
                        pixelColor = new Color(r, g, b, a);
                        break;

                    case GUI_BACKGROUND:
                        int border = 2;

                        // Default pixel color
                        pixelColor = color1;

                        // Brighten top and left edges (bevel highlight)
                        if (x < border || y < border) {
                            pixelColor = brighten(pixelColor, 0.4f);
                        }

                        // Darken bottom and right edges (bevel shadow)
                        if (x >= width - border || y >= height - border) {
                            pixelColor = darken(pixelColor, 0.4f);
                        }

                        // Inner bevel for more depth
                        if ((x == border || y == border) && x < width - border && y < height - border) {
                            pixelColor = brighten(pixelColor, 0.2f);
                        } else if ((x == width - border - 1 || y == height - border - 1) && x >= border && y >= border) {
                            pixelColor = darken(pixelColor, 0.2f);
                        }

                        break;

                    case GUI_BUTTON:
                        // Vertical gradient for depth
                        float gradientRatio = (float) y / height;
                        int rBtn = (int) (color1.getRed() * (1 - gradientRatio) + color2.getRed() * gradientRatio);
                        int gBtn = (int) (color1.getGreen() * (1 - gradientRatio) + color2.getGreen() * gradientRatio);
                        int bBtn = (int) (color1.getBlue() * (1 - gradientRatio) + color2.getBlue() * gradientRatio);
                        int aBtn = (int) (color1.getAlpha() * (1 - gradientRatio) + color2.getAlpha() * gradientRatio);
                        pixelColor = new Color(rBtn, gBtn, bBtn, aBtn);

                        // Bevel effect on top-left and bottom-right
                        if (x < patternSize || y < patternSize) {
                            pixelColor = brighten(pixelColor, 0.2f); // Top-left highlight
                        } else if (x >= width - patternSize || y >= height - patternSize) {
                            pixelColor = darken(pixelColor, 0.2f); // Bottom-right shadow
                        }

                        // Shine in upper third
                        if (y < height / 3) {
                            pixelColor = brighten(pixelColor, 0.05f);
                        }

                        break;

                    case SOLID:
                    default:
                        pixelColor = color1;
                        break;
                }

                image.setRGB(x, y, pixelColor.getRGB());
            }
        }

        DynamicTexture dynamicTexture = new DynamicTexture(image);
        return Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(name, dynamicTexture);
    }

    private static Color brighten(Color color, float amount) {
        int r = Math.min(255, (int) (color.getRed() * (1 + amount)));
        int g = Math.min(255, (int) (color.getGreen() * (1 + amount)));
        int b = Math.min(255, (int) (color.getBlue() * (1 + amount)));
        return new Color(r, g, b, color.getAlpha());
    }

    private static Color darken(Color color, float amount) {
        int r = Math.max(0, (int) (color.getRed() * (1 - amount)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - amount)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - amount)));
        return new Color(r, g, b, color.getAlpha());
    }

}
