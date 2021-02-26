import {AppRegistry} from 'react-native';
import {name as appName} from './app.json';

import React, {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  DeviceEventEmitter,
} from 'react-native';
import {CustomButton, image} from '@src';
import ReplaceDialer from 'react-native-replace-dialer';

import CallState from 'react-native-call-state';

export default class CallActivity extends Component {
  constructor(props) {
    super(props);
    this.state = {
      connected: false,
      callType: '',
    };
    this.startListenerTapped();
  }

  // call state start
  startListenerTapped() {
    CallState.startListener();
    DeviceEventEmitter.addListener('callStateUpdated', (data) => {
      console.log('Call state updated>>', data);
      var event = data.state;

      if (
        event === 'Connected' ||
        event === 'Incoming' ||
        event === 'Dialing' ||
        event === 'Offhook'
      ) {
        this.setState({connected: true, callType: event});
      } else {
        this.setState({connected: false, callType: ''});
      }
    });
  }

  //Call end
  endCall() {
    ReplaceDialer.disconnectCall();
    this.props.navigation.pop();
  }

  incomingView = () => {
    return (
      <View
        style={{
          width: '100%',
          flexDirection: 'row',
          justifyContent: 'space-between',
          padding: 50,
        }}>
        <TouchableOpacity style={{flexDirection: 'column'}} onPress={() => {}}>
          <Image
            style={styles.receiveCall}
            source={{uri: image.recieveCallButton}}
          />
          <Text style={{textAlign: 'center'}}>Accept</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={this.endCall}>
          <Image style={styles.endCall} source={{uri: image.endCallButton}} />
          <Text style={{textAlign: 'center'}}>Decline</Text>
        </TouchableOpacity>
      </View>
    );
  };

  callAnsweredView = () => {
    return (
      <View
        style={{
          flexDirection: 'row',
          justifyContent: 'space-between',
        }}>
        <TouchableOpacity onPress={this.endCall}>
          <Image style={styles.endCall} source={{uri: image.endCallButton}} />
        </TouchableOpacity>
      </View>
    );
  };

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
        {this.incomingView()}
        {/*
        {this.state.callType == 'Incoming'
          ? this.incomingView()
          : this.callAnsweredView()}
          */}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
  },
  endCall: {
    height: '30%',
    aspectRatio: 1,
  },
  receiveCall: {
    height: '26%',
    aspectRatio: 1,
  },
  calling: {
    paddingTop: 10,
    fontSize: 22,
    color: 'gray',
  },
});

AppRegistry.registerComponent(appName, () => CallActivity);
