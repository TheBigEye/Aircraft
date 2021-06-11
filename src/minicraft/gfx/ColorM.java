package minicraft.gfx;

public class ColorM {
	  public static int multiply(int color, double factor) {
	    for (int i = 0; i < 3; i++)
	      color = color & (255 << i * 8 ^ 0xFFFFFFFF) | (int)((color >> i * 8 & 0xFF) * factor) << i * 8; 
	    return color;
	  }
	  
	  public static int divide(int color, double factor) {
	    for (int i = 0; i < 3; i++)
	      color = color & (255 << i * 8 ^ 0xFFFFFFFF) | (int)((color >> i * 8 & 0xFF) / factor) << i * 8; 
	    return color;
	  }
	  
	  public static int multiply(int color1, int color2) {
	    for (int i = 0; i < 3; i++)
	      color1 = color1 & (255 << i * 8 ^ 0xFFFFFFFF) | (int)(((color1 >> i * 8 & 0xFF) * (color2 >> i * 8 & 0xFF)) / 255.0D) << i * 8; 
	    return color1;
	  }
	  
	  public static int divide(int color1, int color2) {
	    for (int i = 0; i < 3; i++) {
	      float c1 = (color1 >> i * 8 & 0xFF) / 255.0F;
	      float c2 = (color2 >> i * 8 & 0xFF) / 255.0F;
	      if (c2 <= c1) {
	        color1 = 255 << i * 8;
	      } else {
	        color1 = color1 & (255 << i * 8 ^ 0xFFFFFFFF) | (int)(((color1 >> i * 8 & 0xFF) / (color2 >> i * 8 & 0xFF)) / 255.0D) << i * 8;
	      } 
	    } 
	    return color1;
	  }
	  
	  public static int add(int color1, int color2) {
	    for (int i = 0; i < 3; i++) {
	      int col = (color1 >> i * 8 & 0xFF) + (color2 >> i * 8 & 0xFF);
	      if (col > 255)
	        col = 255; 
	      color1 = color1 & (255 << i * 8 ^ 0xFFFFFFFF) | col << i * 8;
	    } 
	    return color1;
	  }
	  
	  public static int sub(int color1, int color2) {
	    for (int i = 0; i < 3; i++) {
	      int col = (color1 >> i * 8 & 0xFF) - (color2 >> i * 8 & 0xFF);
	      if (col > 255)
	        col = 255; 
	      color1 = color1 & (255 << i * 8 ^ 0xFFFFFFFF) | col << i * 8;
	    } 
	    return color1;
	  }
	  
	  public static float[] floatColor(int color) {
	    float r = (color >> 16 & 0xFF) / 255.0F;
	    float g = (color >> 8 & 0xFF) / 255.0F;
	    float b = (color & 0xFF) / 255.0F;
	    return new float[] { r, g, b };
	  }
	  
	  public static int intColor(float[] color) {
	    int r = (int)(color[0] * 255.0F);
	    int g = (int)(color[1] * 255.0F);
	    int b = (int)(color[2] * 255.0F);
	    return r << 16 | g << 8 | b;
	  }
	  
	  public static float clip01(float f) {
	    if (f < 0.0F)
	      f = 0.0F; 
	    if (f > 1.0F)
	      f = 1.0F; 
	    return f;
	  }
	  
	  public static double acosangle(int x, int y) {
	    if (x == 0 && y == 0)
	      return 0.0D; 
	    double a = Math.acos(x / Math.sqrt((x * x + y * y)));
	    if (y < 0)
	      a = 6.283185307179586D - a; 
	    return 6.283185307179586D - a;
	  }
	  
	  public static int getVectorColorCircular(double angle, double intensity) {
	    int r = (int)((Math.sin(angle + 1.5707963267948966D) + 0.5D) * intensity);
	    int g = (int)((Math.sin(angle + 1.5707963267948966D - 2.0943951023931953D) + 0.5D) * intensity);
	    int b = (int)((Math.sin(angle + 1.5707963267948966D - 4.1887902047863905D) + 0.5D) * intensity);
	    if (r < 0)
	      r = 0; 
	    if (r > 255)
	      r = 255; 
	    if (g < 0)
	      g = 0; 
	    if (g > 255)
	      g = 255; 
	    if (b < 0)
	      b = 0; 
	    if (b > 255)
	      b = 255; 
	    return (r << 16) + (g << 8) + b;
	  }
	  
	  public static float[] RGB2HSL(float[] rgb) {
	    float s, r = rgb[0];
	    float g = rgb[1];
	    float b = rgb[2];
	    float max = Math.max(Math.max(r, g), b), min = Math.min(Math.min(r, g), b);
	    float h = 0.0F, l = (max + min) / 2.0F;
	    if (max == min) {
	      h = s = 0.0F;
	    } else {
	      float d = max - min;
	      s = (l > 0.5F) ? (d / (2.0F - max - min)) : (d / (max + min));
	      if (max == r) {

	      } else if (max == g) {
	        h = (b - r) / d + 2.0F;
	      } else if (max == b) {
	        h = (r - g) / d + 4.0F;
	      } 
	      h /= 6.0F;
	    } 
	    return new float[] { h, s, l };
	  }
	  
	  public static float[] HSL2RGB(float[] hsl) {
	    float r, g, b, h = hsl[0];
	    float s = hsl[1];
	    float l = hsl[2];
	    if (s == 0.0F) {
	      r = g = b = l;
	    } else {
	      float q = (l < 0.5D) ? (l * (1.0F + s)) : (l + s - l * s);
	      float p = 2.0F * l - q;
	      r = hue2rgb(p, q, h + 0.33333334F);
	      g = hue2rgb(p, q, h);
	      b = hue2rgb(p, q, h - 0.33333334F);
	    } 
	    return new float[] { r, g, b };
	  }
	  
	  private static float hue2rgb(float p, float q, float t) {
	    if (t < 0.0F)
	      t++; 
	    if (t > 1.0F)
	      t--; 
	    if (t < 0.16666667F)
	      return p + (q - p) * 6.0F * t; 
	    if (t < 0.5F)
	      return q; 
	    if (t < 0.6666667F)
	      return p + (q - p) * (0.6666667F - t) * 6.0F; 
	    return p;
	  }
	  
	  public static float[] RGB2HSV(float[] rgb) {
	    float r = rgb[0];
	    float g = rgb[1];
	    float b = rgb[2];
	    float max = Math.max(Math.max(r, g), b), min = Math.min(Math.min(r, g), b);
	    float h = 0.0F, v = max;
	    float d = max - min;
	    float s = (max == 0.0F) ? 0.0F : (d / max);
	    if (max == min) {
	      h = 0.0F;
	    } else {
	      if (max == r) {

	      } else if (max == g) {
	        h = (b - r) / d + 2.0F;
	      } else if (max == b) {
	        h = (r - g) / d + 4.0F;
	      } 
	      h /= 6.0F;
	    } 
	    return new float[] { h, s, v };
	  }
	  
	  public static float[] HSV2RGB(float[] hsv) {
	    float h = hsv[0];
	    float s = hsv[1];
	    float v = hsv[2];
	    float r = 0.0F, g = 0.0F, b = 0.0F;
	    int i = (int)Math.floor((h * 6.0F));
	    float f = h * 6.0F - i;
	    float p = v * (1.0F - s);
	    float q = v * (1.0F - f * s);
	    float t = v * (1.0F - (1.0F - f) * s);
	    switch (i % 6) {
	      case 0:
	        r = v;
	        g = t;
	        b = p;
	        break;
	      case 1:
	        r = q;
	        g = v;
	        b = p;
	        break;
	      case 2:
	        r = p;
	        g = v;
	        b = t;
	        break;
	      case 3:
	        r = p;
	        g = q;
	        b = v;
	        break;
	      case 4:
	        r = t;
	        g = p;
	        b = v;
	        break;
	      case 5:
	        r = v;
	        g = p;
	        b = q;
	        break;
	    } 
	    return new float[] { r, g, b };
	  }
	  
	  public static float[] RGB2HSLc(float[] rgb) {
	    return RGB2HSL(clip01fv(rgb));
	  }
	  
	  public static float[] HSL2RGBc(float[] hsl) {
	    return RGB2HSL(clip01fv(hsl));
	  }
	  
	  public static float[] RGB2HSVc(float[] rgb) {
	    return RGB2HSL(clip01fv(rgb));
	  }
	  
	  public static float[] HSV2RGBc(float[] hsv) {
	    return RGB2HSL(clip01fv(hsv));
	  }
	  
	  public static float[] clip01fv(float[] v) {
	    float[] v2 = new float[3];
	    for (int i = 0; i < v.length; i++)
	      v2[i] = clip01(v[i]); 
	    return v2;
	  }
	  
	  public static void unitTests() {
	    for (float a = 0.0F; a <= 1.0F; a = (float)(a + 0.1D)) {
	      for (float b = 0.0F; b <= 1.0F; b = (float)(b + 0.1D)) {
	        for (float c = 0.0F; c <= 1.0F; c = (float)(c + 0.1D)) {
	          float[] res = RGB2HSL(HSL2RGB(new float[] { a, b, c }));
	          if (res[0] != a && res[1] != b && res[2] != c)
	            System.out.println("difference: a=" + a + ":" + res[0] + " b=" + b + ":" + res[1] + " c=" + c + ":" + res[2]); 
	        } 
	      } 
	    } 
	  }
	  
	  public static void drawColorSpace(int width, int height, int[] pixels, int selection) {
	    double maxlen = (height / 2);
	    for (int y = 0; y < height; y++) {
	      for (int x = 0; x < width; x++) {
	        int colacos, colhsb, colhsv, colhsl, xr = x - width / 2;
	        int yr = y - height / 2;
	        switch (selection) {
	          case 0:
	            colacos = getVectorColorCircular(acosangle(xr, yr), Math.sqrt((xr * xr + yr * yr)));
	            pixels[x + y * width] = colacos;
	            break;
	          case 1:
	            colhsb = intColor(HSV2RGB(new float[] { (float)(acosangle(xr, yr) / Math.PI / 2.0D), (float)(Math.sqrt((xr * xr + yr * yr)) / maxlen), (float)(Math.sqrt((xr * xr + yr * yr)) / maxlen) }));
	            pixels[x + y * width] = colhsb;
	            break;
	          case 2:
	            colhsv = intColor(HSV2RGB(new float[] { (float)(acosangle(xr, yr) / Math.PI / 2.0D), (float)(Math.sqrt((xr * xr + yr * yr)) / maxlen), (float)(Math.sqrt((xr * xr + yr * yr)) / maxlen) }));
	            pixels[x + y * width] = colhsv;
	            break;
	          case 3:
	            colhsl = intColor(HSL2RGB(new float[] { (float)(acosangle(xr, yr) / Math.PI / 2.0D), (float)(Math.sqrt((xr * xr + yr * yr)) / maxlen), (float)(Math.sqrt((xr * xr + yr * yr)) / maxlen) }));
	            pixels[x + y * width] = colhsl;
	            break;
	        } 
	      } 
	    } 
	  }
	  
	  public static void main(String[] args) {}
	}
