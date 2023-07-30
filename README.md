ImageProcessor is a Java class that provides various image processing functionalities. It allows users to load an image, adjust brightness, equalize color histograms, convert to CIE XYZ, and save the modified image.

## Key Features


- **Adjust Brightness**: The class enables users to adjust the brightness of the image by adding a specified brightness value to each pixel's RGB components.
- **Equalize Color Histogram**: The class supports color histogram equalization, which enhances the contrast of the image by redistributing color intensity values.
- **Multithreaded Processing**: Some operations, such as brightness adjustment and histogram equalization, are performed using multiple threads to improve processing efficiency on multi-core processors.
- **Equalize&ToCIEXYZ**:Equalize the histogram by converting the image to CIEXYZ color space and then back to sRGB.
- **Save Modified Image**: After processing the image, users can save the modified version to a specified file path.



<img width="620" alt="Screenshot 2023-07-30 at 06 48 38" src="https://github.com/sirgypsyg/ImageProcessing/assets/107400417/3ab053ec-b410-4af2-b63e-52ea2ec9c713">
Original
<img width="623" alt="Screenshot 2023-07-30 at 06 49 02" src="https://github.com/sirgypsyg/ImageProcessing/assets/107400417/d8b3fe07-c8ee-4fcd-8521-1b90a9a4cc90">
equalized RGB
<img width="621" alt="Screenshot 2023-07-30 at 06 49 29" src="https://github.com/sirgypsyg/ImageProcessing/assets/107400417/35622f4e-9b90-4aed-934b-431c1e4d3179">
equalized CIE XYZ
