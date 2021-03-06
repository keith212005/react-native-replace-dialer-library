import React, {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  DeviceEventEmitter,
  SafeAreaView,
  StatusBar,
} from 'react-native';

import {
  check,
  PERMISSIONS,
  RESULTS,
  checkMultiple,
  requestMultiple,
  request,
} from 'react-native-permissions';

import {CustomButton, MyStopwatch2} from '@components';
import {image, constant} from '@constants';
import {responsiveFonts, responsiveHeight, responsiveWidth} from '@resources';

import ReplaceDialer from 'react-native-replace-dialer';
import CallDetectorManager from 'react-native-call-detection';

const img = [
  image.add_b,
  image.add_g,
  image.hold_b,
  image.hold_g,
  image.record_b,
  image.record_g,
  image.bluetooth_g,
  image.bluetooth_b,
  image.speaker_b,
  image.speaker_g,
  image.mic_b,
  image.mic_g,
  image.keypad_b,
  image.keypad_g,
  image.merge_b,
  image.merge_g,
];

export default class CallScreen extends Component {
  constructor(props) {
    super(props);

    console.log('All Params = ', this.props.route.params);
    // const {callType, phoneNumber} = this.props.route.params;

    this.startCallListener();

    this.state = {
      event: '',
      callType: '',
      stopwatchShow: false,
      stopwatchStart: true,
      stopwatchPause: false,
      phoneNumber: '',
      blutName: constant.BLUT,
      speaker: false,
      merge: false,
      hold: false,
      mute: false,
      record: false,
      keypad: false,
      conference: false,
    };
  }

  stopCallListener() {
    this.callDetector && this.callDetector.dispose();
  }

  componentDidMount() {
    ReplaceDialer.getCallDetails((callType, bluetoothName, phoneNumber) => {
      console.log('calldetails>>>', callType, bluetoothName, phoneNumber);
      this.setState({
        blutName: bluetoothName,
      });
    });
    ReplaceDialer.getPhoneNumber((phoneNumber) => {
      if (phoneNumber != null) {
        this.setState({phoneNumber: phoneNumber});
      }
    });
    ReplaceDialer.getCallType((callType) => {
      if (callType != null) {
        this.setState({callType: callType});
      }
    });
  }

  componentWillUnmount() {
    this.stopCallListener();
  }

  startCallListener() {
    this.callDetector = new CallDetectorManager(
      (event, phoneNumber) => {
        this.setState({event: event});

        // For iOS event will be either "Connected",
        // "Disconnected","Dialing" and "Incoming"

        // For Android event will be either "Offhook",
        // "Disconnected", "Incoming" or "Missed"
        // phoneNumber should store caller/called number

        console.log('startCallListener >> ', event, phoneNumber);
        if (event === 'Disconnected') {
          // Do something call got disconnected
          this.setState({stopwatchPause: true}, () => {
            setTimeout(() => {
              ReplaceDialer.closeCurrentView();
            }, 2000);
          });
        } else if (event === 'Connected') {
          // Do something call got connected
          // This clause will only be executed for iOS
        } else if (event === 'Incoming') {
          // Do something call got incoming
        } else if (event === 'Dialing') {
          // Do something call got dialing
          // This clause will only be executed for iOS
        } else if (event === 'Offhook') {
          //Device call state: Off-hook.
          // At least one call exists that is dialing,
          // active, or on hold,
          // and no calls are ringing or waiting.
          // This clause will only be executed for Android
          this.setState({stopwatchShow: true, stopwatchStart: true}, () => {
            ReplaceDialer.startProximitySensor();
          });
        } else if (event === 'Missed') {
          // Do something call got missed
          // This clause will only be executed for Android
          setTimeout(() => {
            ReplaceDialer.closeCurrentView();
          }, 2000);
        }
      },
      true, // if you want to read the phone number of the incoming call [ANDROID], otherwise false
      (number) => {
        console.log('permission denied', number);
      }, // callback if your permission got denied [ANDROID] [only if you want to read incoming number] default: console.error
      {
        title: 'Phone State Permission',
        message:
          'This app needs access to your phone state in order to react and/or to adapt to incoming calls.',
      }, // a custom permission request message to explain to your user, why you need the permission [recommended] - this is the default one
    );
  }

  _renderIncomingView = () => {
    return (
      <View style={styles.incomingViewContainer}>
        <TouchableOpacity
          style={{flexDirection: 'column'}}
          onPress={() => this._handleCall(constant.ACCEPTCALL)}>
          <Image
            style={styles.receiveCall}
            source={{uri: image.recieveCallButton}}
          />
          <Text style={{textAlign: 'center', marginTop: 5}}>
            {constant.ACCEPTCALL}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => this._handleCall(constant.REJECTCALL)}>
          <Image style={styles.endCall} source={{uri: image.endCallButton}} />
          <Text style={{textAlign: 'center', marginTop: 5}}>
            {constant.REJECTCALL}
          </Text>
        </TouchableOpacity>
      </View>
    );
  };

  _renderCallAnsweredView = () => {
    return (
      <TouchableOpacity onPress={() => this._handleCall(constant.ENDCALL)}>
        <Image style={styles.endCall} source={{uri: image.endCallButton}} />
        <Text style={{textAlign: 'center', marginTop: 5}}>
          {constant.ENDCALL}
        </Text>
      </TouchableOpacity>
    );
  };

  _handleCall = (action) => {
    switch (action) {
      case constant.ACCEPTCALL:
        this.setState({stopwatchShow: true, stopwatchStart: true}, () => {
          ReplaceDialer.acceptCall();
        });
        break;
      case constant.REJECTCALL:
        this.setState({stopwatchStart: false, stopwatchPause: true}, () => {
          ReplaceDialer.disconnectCall();
        });
        break;
      case constant.ENDCALL:
        this.setState({stopwatchStart: false, stopwatchPause: true}, () => {
          // ReplaceDialer.disconnectCall();
          ReplaceDialer.disconnectCall();
        });
        break;
      case constant.ADD:
        this.props.navigation.navigate('DialScreen');
        // ReplaceDialer.makeConferenceCall((value) => {
        //   console.log('makeConferenceCall>>>>', value);
        // });
        break;
      case constant.MERGE:
        ReplaceDialer.mergeCall((value) => {
          // this.setState({merge: value});
          console.log('total >> ', value);
        });
        break;
      case constant.HOLD:
        ReplaceDialer.holdCall((isOnHold) => {
          this.setState({hold: isOnHold});
        });
        break;
      case constant.REC:
        ReplaceDialer.recordCall((isRecording) => {
          this.setState({record: isRecording});
        });
        break;
      case constant.BLUT:
        ReplaceDialer.toggleBluetooth();
        break;
      case constant.SPEAKER:
        ReplaceDialer.toggleSpeaker((isSpeakerOn) => {
          this.setState({speaker: isSpeakerOn});
        });
        break;
      case constant.MUTE:
        ReplaceDialer.toggleMute((isMicMute) => {
          this.setState({mute: isMicMute});
        });
        break;
      case constant.KEYPAD:
        break;
      default:
    }
  };

  _renderCtrls = (name, imageUri, image1, image2) => {
    return (
      <CustomButton
        name={name}
        imageUri={imageUri ? image1 : image2}
        imageStyle={{height: responsiveHeight(5)}}
        onPress={() => this._handleCall(name)}
        buttonStyle={{padding: 30}}
      />
    );
  };

  render() {
    const {
      speaker,
      mute,
      stopwatchStart,
      stopwatchPause,
      stopwatchShow,
      event,
      record,
      phoneNumber,
      keypad,
      hold,
      conference,
      callType,
      blutName,
      merge,
    } = this.state;

    return (
      <>
        <StatusBar barStyle={'dark-content'} />
        <SafeAreaView style={styles.container}>
          <View style={styles.subContainer1}>
            <Text style={styles.calling}>{callType}</Text>

            <Text style={styles.phoneNumber}>
              {phoneNumber ? phoneNumber : ''}
            </Text>

            {/* Show timer when user pick up call */}
            {stopwatchShow && (
              <MyStopwatch2 start={stopwatchStart} pause={stopwatchPause} />
            )}

            <Text>
              {event == 'Disconnected' || event == 'Missed' ? event : ''}
            </Text>
          </View>

          <View style={styles.subContainer2}>
            {callType != 'Incoming' || event == 'Offhook' ? (
              <>
                <View style={styles.row}>
                  {this._renderCtrls(constant.ADD, conference, img[0], img[1])}
                  {this._renderCtrls(constant.HOLD, hold, img[2], img[3])}
                  {this._renderCtrls(constant.REC, record, img[4], img[5])}
                  {this._renderCtrls(blutName, blutName, img[6], img[7])}
                </View>
                <View style={styles.row}>
                  {this._renderCtrls(constant.SPEAKER, speaker, img[8], img[9])}
                  {this._renderCtrls(constant.MUTE, mute, img[10], img[11])}
                  {this._renderCtrls(constant.KEYPAD, keypad, img[12], img[13])}
                  {this._renderCtrls(constant.MERGE, merge, img[14], img[15])}
                </View>
              </>
            ) : null}
          </View>

          <View style={styles.subContainer3}>
            {callType == 'Incoming' && event == 'Incoming'
              ? this._renderIncomingView()
              : event == 'Missed'
              ? this._renderIncomingView()
              : this._renderCallAnsweredView()}
          </View>
        </SafeAreaView>
      </>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: responsiveHeight(10),
    alignItems: 'center',
    justifyContent: 'center',
  },
  endCall: {
    height: responsiveHeight(8),
    aspectRatio: 1,
  },
  receiveCall: {
    height: responsiveHeight(8),
    aspectRatio: 1,
  },
  calling: {
    textAlign: 'center',
    fontSize: responsiveFonts(16),
    color: 'gray',
  },
  row: {
    flexDirection: 'row',
    width: '100%',
    justifyContent: 'space-evenly',
    alignItems: 'center',
  },
  incomingViewContainer: {
    width: '100%',
    flexDirection: 'row',
    justifyContent: 'space-evenly',
  },
  phoneNumber: {
    fontSize: responsiveFonts(26),
  },
  subContainer1: {
    flex: 2,
    alignItems: 'center',
  },
  subContainer2: {
    flex: 6,
    justifyContent: 'center',
    alignItems: 'center',
  },
  subContainer3: {
    flex: 2,
  },
});
