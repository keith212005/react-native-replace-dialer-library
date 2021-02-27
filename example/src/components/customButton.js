import React, {Component} from 'react';
import {TouchableOpacity, Text, StyleSheet, Image, View} from 'react-native';

export default class CustomButton extends Component {
  render() {
    const {name, imageUri, onPress, imageStyle} = this.props;
    return (
      <TouchableOpacity style={styles.button} onPress={onPress}>
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
    padding: 15,
  },
  image: {
    aspectRatio: 1,
  },
  name: {
    fontSize: 12,
    color: 'gray',
  },
});
