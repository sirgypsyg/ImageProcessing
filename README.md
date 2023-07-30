# ImageProcessor
ImageProcessor is a Java class that provides various image processing functionalities. It allows users to load an image, adjust brightness, equalize color histograms, convert to CIE XYZ, and save the modified image.

## Key Features


- **Adjust Brightness**: The class enables users to adjust the brightness of the image by adding a specified brightness value to each pixel's RGB components.
- **Equalize Color Histogram**: The class supports color histogram equalization, which enhances the contrast of the image by redistributing color intensity values.
- **Multithreaded Processing**: Some operations, such as brightness adjustment and histogram equalization, are performed using multiple threads to improve processing efficiency on multi-core processors.
- **Equalize&ToCIEXYZ**:Equalize the histogram by converting the image to CIEXYZ color space and then back to sRGB.
- **Save Modified Image**: After processing the image, users can save the modified version to a specified file path.


