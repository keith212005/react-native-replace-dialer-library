import {Dimensions, PixelRatio} from 'react-native';
const {height, width} = Dimensions.get('window');
// based on iphone 5s's scale
const scale = width / 320;

// get responsiveHeight
export const responsiveHeight = (h) => {
  return PixelRatio.roundToNearestPixel(height * (h / 100));
};

// get responsiveWidth
export const responsiveWidth = (w) => {
  return PixelRatio.roundToNearestPixel(width * (w / 100));
};

// get responsiveFonts
export function responsiveFonts(size) {
  const newSize = size * scale;
  if (Platform.OS === 'ios') {
    return Math.round(PixelRatio.roundToNearestPixel(newSize));
  } else {
    return Math.round(PixelRatio.roundToNearestPixel(newSize)) - 2;
  }
}
