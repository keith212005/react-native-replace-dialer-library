import React, {Component} from 'react';
import {View, Text, StyleSheet, TouchableOpacity} from 'react-native';

import {responsiveFonts} from '@resources';

export default class KeypadView extends Component {
  onPress = (value) => {
    this.props.onKeypadPress(value);
  };

  renderButton = (value) => {
    return (
      <TouchableOpacity
        style={styles.button}
        onPress={() => this.onPress(value)}>
        <Text style={styles.text}>{value}</Text>
      </TouchableOpacity>
    );
  };

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.row}>
          {this.renderButton('1')}
          {this.renderButton('2')}
          {this.renderButton('3')}
        </View>
        <View style={styles.row}>
          {this.renderButton('4')}
          {this.renderButton('5')}
          {this.renderButton('6')}
        </View>
        <View style={styles.row}>
          {this.renderButton('7')}
          {this.renderButton('8')}
          {this.renderButton('9')}
        </View>
        <View style={styles.row}>
          {this.renderButton('*')}
          {this.renderButton('0')}
          {this.renderButton('#')}
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {width: '100%'},
  row: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
  },
  button: {
    padding: 30,
  },
  text: {
    fontSize: responsiveFonts(25),
  },
});
