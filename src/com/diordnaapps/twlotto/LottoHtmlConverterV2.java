package com.diordnaapps.twlotto;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;

public class LottoHtmlConverterV2 implements ContentHandler {
    private Context mContext;
    private String mSource;
    private InputSource mInputSource;
    private XMLReader mReader;
    
    // 2012-12-19, Yu-An Chen
    // add some member variables in WinningInfoV2
    private WinningInfoV2 mWinningInfo;
    private Vector<WinningInfoV2> mVector = new Vector<WinningInfoV2>();
//    private WinningInfoV2 mWinningInfo;
//    private Vector<WinningInfoV2> mVector = new Vector<WinningInfoV2>();
    
    private SAXException mSAXException;
    private String mLatestDrawId;
    
    // 2012-12-19, Yu-An Chen
    // new added for parsing invoice web
    private static final String SPANCLASSATTR_STRING = "t18Red";
    
    int index = 0;
    int parseType;

    StringBuffer sbn = new StringBuffer();
    //For Lotto
    String spanID = new String();
    //For Invoice
    String spanClass = new String();
    String divClass = new String();
    Boolean bWinNoFound = false;
    Boolean bGrandPrize = false; // to check if the grand prize exists
    //For 3D and 4D
    Boolean bStarLottoTag = false;
    String startDrawID;
    Boolean bStartDrawDate = false;
    String startDrawDate;
    Boolean bStartCashDate = false;
    Boolean bStartPrizeFlag = false;
    
    public LottoHtmlConverterV2(Context context, String source, Parser parser, 
            String drawId, int type) {
        mContext = context;
        mSource = source;
        mReader = parser;
        index = 0;
        parseType = type;
        mLatestDrawId = drawId;
        mWinningInfo = new WinningInfoV2();
        mVector = new Vector<WinningInfoV2>();
//        mWinningInfo = new WinningInfo();
//        mVector = new Vector<WinningInfo>();
    }
    
    public LottoHtmlConverterV2(Context context, InputSource source, Parser parser, 
            String drawId, int type) {
        mContext = context;
        mInputSource = source;
        mReader = parser;
        index = 0;
        parseType = type;
        mLatestDrawId = drawId;
        mWinningInfo = new WinningInfoV2();
        mVector = new Vector<WinningInfoV2>();
//        mWinningInfo = new WinningInfo();
//        mVector = new Vector<WinningInfo>();
    }

    public Vector<WinningInfoV2> convert() {
    	
        mReader.setContentHandler(this);
        try {
            if (mSource != null)
                mReader.parse(new InputSource(new StringReader(mSource)));
            else
                mReader.parse(mInputSource);
        } catch (IOException e) {
            // We are reading from a string. There should not be IO problems.
            throw new RuntimeException(e);
        } catch (SAXException e) {
            if (!e.equals(mSAXException)) {
                // TagSoup doesn't throw parse exceptions.
                throw new RuntimeException(e);
            }
        }             
        
        // 2012-12-19, Yu-An Chen
        // add some member variables in WinningInfoV2
        // add one more comparison
        // add last record if we are dealing with invoice info       
        if( parseType == WinningInfoV2.PARSE_INVOICE && mWinningInfo.drawID != null)
        {
            // set invoice winning prize amount 
        	setInvoicePrizeAmount(); // the new added
        	mVector.add( mWinningInfo );        
        }
        
      //Add last record
//      if (mWinningInfo.drawID != null)
//          mVector.add(mWinningInfo);
        
        return mVector;
    }

    // deal with the characters in the element
    // we can get contents from the arguments
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        sbn.delete(0, sbn.length());
        sbn.append(ch, start, length);
        // LottoLog.log("CHARACTER is: " + sbn.toString());
        
        
        // 2012-12-19, Yu-An Chen
        // add some member variables in WinningInfoV2
        // Only check if parsing Invoice winning no
        if (parseType == WinningInfoV2.PARSE_INVOICE) {
            if (sbn.toString()
                    .equals(mContext
                            .getString(R.string.convert_check_invoice_grandprize))) {
                bGrandPrize = true;
            }
        }
        
//        if (parseType == WinningInfo.PARSE_INVOICE) {
//            if (sbn.toString()
//                    .equals(mContext
//                            .getString(R.string.convert_check_invoice_grandprize))) {
//                bGrandPrize = true;
//            }
//        }
    }

    @Override
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        String info = sbn.toString();
//        LottoLog.log("CHARACTER is: " + sbn.toString());
//        if( sbn.toString().contains("獎金") )
//         {
//            @SuppressWarnings("unused")
//		     String teString = null;
//         }
        //For all Lotto types except 3D and 4D
        if (spanID != null && info != null)
        {
            String historyQuery = "Control_history1_dlQuery_ctl0";
            
            if (!spanID.contains(historyQuery))
                return;
            
            // Draw ID
            if (spanID.endsWith("DrawTerm")) {
                int idx = Character.getNumericValue(
                          spanID.charAt(spanID.lastIndexOf(historyQuery) + 
                          historyQuery.length()));
                if (index != idx) {
                    mVector.add(mWinningInfo);
                    mWinningInfo = new WinningInfoV2();
//                    mWinningInfo = new WinningInfo();
                    index = idx;
                }
                if (mWinningInfo.drawID == null) mWinningInfo.drawID = info;
                
                if (mWinningInfo.drawID.equals(mLatestDrawId)) {
                    mSAXException = new SAXException();
                    throw mSAXException;
                }
            }
            
            // Draw Date
            if (spanID.endsWith("Date") || spanID.endsWith("DDate"))
                if (mWinningInfo.drawDate == null) mWinningInfo.drawDate = info;
            if (spanID.endsWith("EDate"))
                if (mWinningInfo.cashDate == null) mWinningInfo.cashDate = info;
            
            // Winning Numbers
            String section1 = "_SNo";
            for (int i = 0; i < 6; i++) {
                String tmp = section1 + (i + 1);
                if (spanID.endsWith(tmp)) {
                    if (mWinningInfo.sec1WinningNo[i] == null)
                        mWinningInfo.sec1WinningNo[i] = info;
                    break;
                }
            }
            if (spanID.endsWith("_SNo7"))
                if (mWinningInfo.sec2WinningNo[0] == null) mWinningInfo.sec2WinningNo[0] = info;

            // Prize
            if (spanID.endsWith("_CategA4"))
                if (mWinningInfo.prize[0] == null) mWinningInfo.prize[0] = info;
            if (spanID.endsWith("_CategB4"))
                if (mWinningInfo.prize[1] == null) mWinningInfo.prize[1] = info;
            if (spanID.endsWith("_CategC4"))
                if (mWinningInfo.prize[2] == null) mWinningInfo.prize[2] = info;
            if (spanID.endsWith("_CategD4"))
                if (mWinningInfo.prize[3] == null) mWinningInfo.prize[3] = info;
            if (spanID.endsWith("_CategE4"))
                if (mWinningInfo.prize[4] == null) mWinningInfo.prize[4] = info;
            if (spanID.endsWith("_CategF4"))
                if (mWinningInfo.prize[5] == null) mWinningInfo.prize[5] = info;
            if (spanID.endsWith("_CategG4"))
                if (mWinningInfo.prize[6] == null) mWinningInfo.prize[6] = info;
            if (spanID.endsWith("_CategH4"))
                if (mWinningInfo.prize[7] == null) mWinningInfo.prize[7] = info;
            if (spanID.endsWith("_CategJ4"))
                if (mWinningInfo.prize[8] == null) mWinningInfo.prize[8] = info;
            if (spanID.endsWith("_CategI4"))
                if (mWinningInfo.prize[9] == null) mWinningInfo.prize[9] = info;
            
            // Prize
            if (spanID.endsWith("_CategA1"))
                if (mWinningInfo.prize[0] == null) mWinningInfo.prize[0] = info;
            if (spanID.endsWith("_CategB1"))
                if (mWinningInfo.prize[1] == null) mWinningInfo.prize[1] = info;
            if (spanID.endsWith("_CategC1"))
                if (mWinningInfo.prize[2] == null) mWinningInfo.prize[2] = info;
            if (spanID.endsWith("_CategD1"))
                if (mWinningInfo.prize[3] == null) mWinningInfo.prize[3] = info;
            
            // Prize for 39 & 49 lotto
            if (spanID.endsWith("_M539_CategA3") || spanID.endsWith("_M649_CategA3"))
                if (mWinningInfo.prize[0] == null) mWinningInfo.prize[0] = info;
            if (spanID.endsWith("_M539_CategB3") || spanID.endsWith("_M649_CategB3"))
                if (mWinningInfo.prize[1] == null) mWinningInfo.prize[1] = info;
            if (spanID.endsWith("_M539_CategC3") || spanID.endsWith("_M649_CategC3"))
                if (mWinningInfo.prize[2] == null) mWinningInfo.prize[2] = info;
            
            // For Lotto649 section 2 winning number
            if (spanID.endsWith("_SNo"))
                if (mWinningInfo.sec2WinningNo[0] == null) mWinningInfo.sec2WinningNo[0] = info;
            
            //For TTT
            String label = "_Label";
            for (int j = 2; j <= 9; j++) {
                String tmp = label + (j);
                if (spanID.endsWith(tmp)) {
                    if (mWinningInfo.sec1WinningNo[j - 1] == null)
                        mWinningInfo.sec1WinningNo[j - 1] = info;
                    break;
                }
            }
        }
        
        //For Lotto types 3D and 4D
        if (bStarLottoTag) {
            startDrawID = info;
            bStarLottoTag = false;
         }
        if (bStartDrawDate) {
            startDrawDate = info;
            bStartDrawDate = false;
         }
        if (info.contains(mContext.getString(R.string.convert_check_drawdate_slashmark))
        		&& localName.equals("p")) {
            bStartDrawDate = true;
         }
        if (localName.equals("div") && divClass != null && info != null) {
            if (divClass.equals("number")) {
                if (mWinningInfo.drawID != null && mWinningInfo.drawID != startDrawID) {
                    mVector.add(mWinningInfo);
                    mWinningInfo = new WinningInfoV2();
//                    mWinningInfo = new WinningInfo();
                  }
                mWinningInfo.drawID = startDrawID;
                mWinningInfo.drawDate = startDrawDate;
                for (int i = 0; i < 4; i++) {
                    if (mWinningInfo.sec1WinningNo[i] == null) {
                        mWinningInfo.sec1WinningNo[i] = info;
                        break;
                      }
                  }
              }
         }
        if (bStartCashDate) {
            mWinningInfo.cashDate = info;
            bStartCashDate = false;
        }
        if (info.equals(mContext.getString(R.string.convert_check_drawcashdate))) {
            bStartCashDate = true;
        }
        if (bStartPrizeFlag == true && localName.equals("br") && 
                info.equals(mContext.getString(R.string.convert_check_prizemoney))) {
            bStartPrizeFlag = false;
        }
        if (bStartPrizeFlag && localName.equals("center") && info != null) {
            if (!info.equals(mContext.getString(R.string.convert_check_prize))) {
                for (int i = 0; i < 5; i++) {
                    if (mWinningInfo.prize[i] == null) {
                        mWinningInfo.prize[i] = info;
                        break;
                    }
                }
            }
        }
        if (localName.equals("br") && info.equals(
                mContext.getString(R.string.convert_check_singledraw))) {
            bStartPrizeFlag = true;
        }
        
        // 2012-12-19, Yu-An Chen
        // because the format of web has changed
        // we have to parse two issue of invoice info
        // store their title as id for each issue
        
        //For Invoice        
        if( info.contains(mContext.getString(R.string.invoice_title_check)) )
         {
        	 if( mWinningInfo.drawID == null )
        	 {
        		 mWinningInfo.drawID = info;
        	 }
        	 else if( mWinningInfo.drawID2 == null )
        	 {
			     mWinningInfo.drawID2 = info;	
			 }
         }     
        
//        if (divClass != null && info != null) {
//            if (divClass.equals("caption")) {
//                mWinningInfo.drawID = info;
//            }
//        }
        
        // 2012-12-19, Yu-An Chen
        // because the format of web has changed
        // we have to parse two issue of invoice info
        
        // 2013-11-26, Yuan, Winning info order problem +
        // find winning numbers, store them
        if (localName.equals("span") && spanClass != null && info != null) {
            if (spanClass.equals(SPANCLASSATTR_STRING)) {
                bWinNoFound = true;
                if (mWinningInfo.grandPrizeNo2[0] == null && bGrandPrize) {
                    insertInvoiceNo(mWinningInfo.grandPrizeNo2, info);
                }
                else if (mWinningInfo.topPrizeNo2[0] == null) {
                    insertInvoiceNo(mWinningInfo.topPrizeNo2, info);
                }
                else if (mWinningInfo.firstPrizeNo2[0] == null) {
                    insertInvoiceNo(mWinningInfo.firstPrizeNo2, info);
                }
                else if (mWinningInfo.sixthPrizeNo2[0] == null) {
                    insertInvoiceNo(mWinningInfo.sixthPrizeNo2, info);
                }
                
                // new added variables for the previous issue invoice info
                else if (mWinningInfo.grandPrizeNo[0] == null && bGrandPrize) {
                    insertInvoiceNo(mWinningInfo.grandPrizeNo, info);
                }
                else if (mWinningInfo.topPrizeNo[0] == null) {
                    insertInvoiceNo(mWinningInfo.topPrizeNo, info);
                }
                else if (mWinningInfo.firstPrizeNo[0] == null) {
                    insertInvoiceNo(mWinningInfo.firstPrizeNo, info);
                }
                else if (mWinningInfo.sixthPrizeNo[0] == null) {
                    insertInvoiceNo(mWinningInfo.sixthPrizeNo, info);
                }
            }
        }
        // 2013-11-26, Yuan, Winning info order problem -
        
//        if (localName.equals("span") && spanClass != null && info != null) {
//            if (spanClass.equals("number")) {
//                bWinNoFound = true;
//                if (mWinningInfo.grandPrizeNo[0] == null && bGrandPrize) {
//                    insertInvoiceNo(mWinningInfo.grandPrizeNo, info);
//                } else if (mWinningInfo.topPrizeNo[0] == null) {
//                    insertInvoiceNo(mWinningInfo.topPrizeNo, info);
//                } else if (mWinningInfo.firstPrizeNo[0] == null) {
//                    insertInvoiceNo(mWinningInfo.firstPrizeNo, info);
//                } else if (mWinningInfo.sixthPrizeNo[0] == null) {
//                    insertInvoiceNo(mWinningInfo.sixthPrizeNo, info);
//                }
//            }
//        }
        
        // 2012-12-19, Yu-An Chen
        // change strategy to a new added method at the end of the file
//        if (bWinNoFound && localName.equals("span") && spanClass != null && info != null) {
//            if (spanClass.equals("amount")) {
//                bWinNoFound = false;
//                if (mWinningInfo.grandPrize == null && bGrandPrize) {
//                    mWinningInfo.grandPrize = info;
//                } else if (mWinningInfo.topPrize == null) {
//                    mWinningInfo.topPrize = info;
//                } else if (mWinningInfo.firstPrize == null) {
//                    mWinningInfo.firstPrize = info;
//                } else if (mWinningInfo.sixthPrize == null) {
//                    mWinningInfo.sixthPrize = info;
//                }
//            }
//        }
        
        
    }
    
    
    private void insertInvoiceNo(String[] winInfo, String info) {
        int i = 0;
        String winNo;
        
        int currentIdx = 0;
        int idx = info.indexOf(mContext.getString(R.string.convert_check_invoice_sep));
        winNo = info.substring(currentIdx, info.length());
        
        while (idx != -1) {
            winInfo[i] = winNo.substring(0, idx);
            currentIdx = currentIdx + idx + 1;
            winNo = info.substring(currentIdx, info.length());
            idx = winNo.indexOf(mContext.getString(R.string.convert_check_invoice_sep));
            i++;
        }
        winInfo[i] = winNo.substring(0, winNo.length());
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        spanID = null;
        spanClass = null;
        divClass = null;
        LottoLog.log("LocalName is: " + localName);
        if (localName.equals("span")) {
            // For Lotto
            spanID = atts.getValue("id");
            // For Invoice, get the invoice winning number
            spanClass = atts.getValue("class");            
        }
        
        // 2012-12-19, Yu-An Chen
        // change parsing strategy 
        else if (localName.equals("br")) {
            // For Invoice
            divClass = atts.getValue("class");            
        }
//        else if (localName.equals("div")) {
//            // For Invoice
//            divClass = atts.getValue("class");            
//        }
        
        // 2012-12-19, Yu-An Chen
        // add more condition to get the 3D or 4D winning number correctly
        else if (localName.equals("tr") || localName.equals("div")) {
            // For 3D and 4D
            String classInfo = atts.getValue("class");
            if (classInfo != null) {
                if (atts.getValue("class").equals("table_level_2_big")
                        || atts.getValue("class").equals("table_level_1_big"))
                    bStarLottoTag = true;
                if (classInfo.equals("number")) {
                    divClass = classInfo;
                }
            }
        }
        
//        else if(localName.equals("tr")) {
//            //For 3D and 4D
//            String classInfo = atts.getValue("class");
//            if (classInfo != null) {
//                if(atts.getValue("class").equals("table_level_2_big") || 
//                        atts.getValue("class").equals("table_level_1_big"))
//                    bStarLottoTag = true;
//            }
//        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        // TODO Auto-generated method stub
        
    }
    
    // 2012-12-19, Yu-An Chen
    // new added method
    // add some invoice winning prize string in string.xml
    private void setInvoicePrizeAmount()
    {
        mWinningInfo.grandPrize = mContext.getString(R.string.invoice_winning_grand_prize);
        mWinningInfo.topPrize = mContext.getString(R.string.invoice_winning_top_prize);
        mWinningInfo.firstPrize = mContext.getString(R.string.invoice_winning_first_prize);
        mWinningInfo.sixthPrize = mContext.getString(R.string.invoice_winning_200);
    }
}
