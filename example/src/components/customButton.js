import React, {Component} from 'react';
import {TouchableOpacity, Text, StyleSheet, Image, View} from 'react-native';
import {responsiveFonts, responsiveWidth, responsiveHeight} from '@resources';

export default class CustomButton extends Component {
  render() {
    const {name, imageUri, onPress, imageStyle, buttonStyle} = this.props;
    return (
      <TouchableOpacity style={[styles.button, buttonStyle]} onPress={onPress}>
        <Image
          style={[styles.image, imageStyle]}
          source={{
            uri: imageUri,
          }}
        />
        <Text style={styles.name}>{name}</Text>
      </TouchableOpacity>
    );
  }
}

const styles = StyleSheet.create({
  button: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  image: {
    aspectRatio: 1,
  },
  name: {
    fontSize: responsiveFonts(12),
    color: 'gray',
  },
});
