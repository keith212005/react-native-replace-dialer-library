import React, {Component} from 'react';
import {TouchableOpacity, Text, StyleSheet, Image} from 'react-native';

export default class CustomButton extends Component {
  render() {
    const {name, imageUri} = this.props;
    return (
      <TouchableOpacity style={styles.container}>
        <Image
          style={[styles.plusImage]}
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
  container: {
    flex: 1,
    alignItems: 'center',
  },
  plusImage: {
    height: '23%',
    aspectRatio: 1,
    borderWidth: 1,
  },
  name: {
    fontSize: 12,
  },
});
