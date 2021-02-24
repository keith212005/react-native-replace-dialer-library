import {AppRegistry} from 'react-native';
import {name as appName} from './app.json';

import React, {Component} from 'react';
import {View, Text, StyleSheet, TouchableOpacity, Image} from 'react-native';
import {CustomButton, image} from '@src';
import ReplaceDialer from 'react-native-replace-dialer';

export default class CallActivity extends Component {
  onPressHangUp() {
    //end call here
    console.log('hangup pressed...');
    ReplaceDialer.endCall((message) => {
      console.log(message);
    });
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={[styles.calling]}>Calling...</Text>
        <View style={{flexDirection: 'row', marginTop: 200}}>
          <CustomButton name="Add" imageUri={image.add} />
          <CustomButton name="Video Call" imageUri={image.videocall} />
          <CustomButton name="Bluetooth" imageUri={image.bluetooth} />
        </View>
        <View style={{flexDirection: 'row'}}>
          <CustomButton name="Speaker" imageUri={image.speaker} />
          <CustomButton name="Mute" imageUri={image.mute} />
          <CustomButton name="Keypad" imageUri={image.keypad} />
        </View>
        <TouchableOpacity onPress={this.onPressHangUp}>
          <Image
            style={styles.endCall}
            source={{
              uri:
                'https://play-lh.googleusercontent.com/AJdc5MZScZb4Yk6tx_6gjfLReztRaHFujar2CUcnsyf24RAwUW9yLbsAb7h2ZLrpLQ',
            }}
          />
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
  },
  endCall: {
    height: '32%',
    aspectRatio: 1,
  },
  calling: {
    paddingTop: 10,
    fontSize: 22,
    color: 'gray',
  },
});

AppRegistry.registerComponent(appName, () => CallActivity);
