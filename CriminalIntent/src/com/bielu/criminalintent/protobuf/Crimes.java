package com.bielu.criminalintent.protobuf;

// Generated by the protocol buffer compiler.  DO NOT EDIT!

public interface Crimes {

  public static final class CrimeList extends
      com.google.protobuf.nano.MessageNano {

    public static final class Crime extends
        com.google.protobuf.nano.MessageNano {

      private static volatile Crime[] _emptyArray;
      public static Crime[] emptyArray() {
        // Lazily initializes the empty array
        if (_emptyArray == null) {
          synchronized (
              com.google.protobuf.nano.InternalNano.LAZY_INIT_LOCK) {
            if (_emptyArray == null) {
              _emptyArray = new Crime[0];
            }
          }
        }
        return _emptyArray;
      }

      // optional string uuid = 1;
      public java.lang.String uuid;

      // optional string title = 2;
      public java.lang.String title;

      // optional bool solved = 3;
      public boolean solved;

      // optional string date = 4;
      public java.lang.String date;

      public Crime() {
        clear();
      }

      public Crime clear() {
        uuid = "";
        title = "";
        solved = false;
        date = "";
        cachedSize = -1;
        return this;
      }

      @Override
      public void writeTo(com.google.protobuf.nano.CodedOutputByteBufferNano output)
          throws java.io.IOException {
        if (!this.uuid.equals("")) {
          output.writeString(1, this.uuid);
        }
        if (!this.title.equals("")) {
          output.writeString(2, this.title);
        }
        if (this.solved != false) {
          output.writeBool(3, this.solved);
        }
        if (!this.date.equals("")) {
          output.writeString(4, this.date);
        }
        super.writeTo(output);
      }

      @Override
      protected int computeSerializedSize() {
        int size = super.computeSerializedSize();
        if (!this.uuid.equals("")) {
          size += com.google.protobuf.nano.CodedOutputByteBufferNano
              .computeStringSize(1, this.uuid);
        }
        if (!this.title.equals("")) {
          size += com.google.protobuf.nano.CodedOutputByteBufferNano
              .computeStringSize(2, this.title);
        }
        if (this.solved != false) {
          size += com.google.protobuf.nano.CodedOutputByteBufferNano
              .computeBoolSize(3, this.solved);
        }
        if (!this.date.equals("")) {
          size += com.google.protobuf.nano.CodedOutputByteBufferNano
              .computeStringSize(4, this.date);
        }
        return size;
      }

      @Override
      public Crime mergeFrom(
              com.google.protobuf.nano.CodedInputByteBufferNano input)
          throws java.io.IOException {
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              return this;
            default: {
              if (!com.google.protobuf.nano.WireFormatNano.parseUnknownField(input, tag)) {
                return this;
              }
              break;
            }
            case 10: {
              this.uuid = input.readString();
              break;
            }
            case 18: {
              this.title = input.readString();
              break;
            }
            case 24: {
              this.solved = input.readBool();
              break;
            }
            case 34: {
              this.date = input.readString();
              break;
            }
          }
        }
      }

      public static Crime parseFrom(byte[] data)
          throws com.google.protobuf.nano.InvalidProtocolBufferNanoException {
        return com.google.protobuf.nano.MessageNano.mergeFrom(new Crime(), data);
      }

      public static Crime parseFrom(
              com.google.protobuf.nano.CodedInputByteBufferNano input)
          throws java.io.IOException {
        return new Crime().mergeFrom(input);
      }
    }

    private static volatile CrimeList[] _emptyArray;
    public static CrimeList[] emptyArray() {
      // Lazily initializes the empty array
      if (_emptyArray == null) {
        synchronized (
            com.google.protobuf.nano.InternalNano.LAZY_INIT_LOCK) {
          if (_emptyArray == null) {
            _emptyArray = new CrimeList[0];
          }
        }
      }
      return _emptyArray;
    }

    // repeated .CrimeList.Crime crimes = 1;
    public Crimes.CrimeList.Crime[] crimes;

    public CrimeList() {
      clear();
    }

    public CrimeList clear() {
      crimes = Crimes.CrimeList.Crime.emptyArray();
      cachedSize = -1;
      return this;
    }

    @Override
    public void writeTo(com.google.protobuf.nano.CodedOutputByteBufferNano output)
        throws java.io.IOException {
      if (this.crimes != null && this.crimes.length > 0) {
        for (int i = 0; i < this.crimes.length; i++) {
          Crimes.CrimeList.Crime element = this.crimes[i];
          if (element != null) {
            output.writeMessage(1, element);
          }
        }
      }
      super.writeTo(output);
    }

    @Override
    protected int computeSerializedSize() {
      int size = super.computeSerializedSize();
      if (this.crimes != null && this.crimes.length > 0) {
        for (int i = 0; i < this.crimes.length; i++) {
          Crimes.CrimeList.Crime element = this.crimes[i];
          if (element != null) {
            size += com.google.protobuf.nano.CodedOutputByteBufferNano
              .computeMessageSize(1, element);
          }
        }
      }
      return size;
    }

    @Override
    public CrimeList mergeFrom(
            com.google.protobuf.nano.CodedInputByteBufferNano input)
        throws java.io.IOException {
      while (true) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            return this;
          default: {
            if (!com.google.protobuf.nano.WireFormatNano.parseUnknownField(input, tag)) {
              return this;
            }
            break;
          }
          case 10: {
            int arrayLength = com.google.protobuf.nano.WireFormatNano
                .getRepeatedFieldArrayLength(input, 10);
            int i = this.crimes == null ? 0 : this.crimes.length;
            Crimes.CrimeList.Crime[] newArray =
                new Crimes.CrimeList.Crime[i + arrayLength];
            if (i != 0) {
              java.lang.System.arraycopy(this.crimes, 0, newArray, 0, i);
            }
            for (; i < newArray.length - 1; i++) {
              newArray[i] = new Crimes.CrimeList.Crime();
              input.readMessage(newArray[i]);
              input.readTag();
            }
            // Last one without readTag.
            newArray[i] = new Crimes.CrimeList.Crime();
            input.readMessage(newArray[i]);
            this.crimes = newArray;
            break;
          }
        }
      }
    }

    public static CrimeList parseFrom(byte[] data)
        throws com.google.protobuf.nano.InvalidProtocolBufferNanoException {
      return com.google.protobuf.nano.MessageNano.mergeFrom(new CrimeList(), data);
    }

    public static CrimeList parseFrom(
            com.google.protobuf.nano.CodedInputByteBufferNano input)
        throws java.io.IOException {
      return new CrimeList().mergeFrom(input);
    }
  }
}
