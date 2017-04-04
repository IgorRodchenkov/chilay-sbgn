package org.ivis.layout;

import java.io.Serializable;

import org.ivis.layout.cose.CoSEConstants;

/**
 * This method gathers the user-customizable layout options in a package
 *
 * @author Cihan Kucukkececi
 * @author Ugur Dogrusoz
 *
 * Copyright: i-Vis Research Group, Bilkent University, 2007 - present
 */
public class LayoutOptionsPack implements Serializable
{
	private static LayoutOptionsPack instance;

	private General general;
	private CoSE coSE;

	public class General
	{
		public int layoutQuality; // proof, default, draft
		public boolean animationDuringLayout; // T-F
		public boolean animationOnLayout; // T-F
		public int animationPeriod; // 0-100
		public boolean incremental; // T-F
		public boolean createBendsAsNeeded; // T-F
		public boolean uniformLeafNodeSizes; // T-F

		public int defaultLayoutQuality = LayoutConstants.DEFAULT_QUALITY;
		public boolean defaultAnimationDuringLayout = LayoutConstants.DEFAULT_ANIMATION_DURING_LAYOUT;
		public boolean defaultAnimationOnLayout = LayoutConstants.DEFAULT_ANIMATION_ON_LAYOUT;
		public int defaultAnimationPeriod = 50;
		public boolean defaultIncremental = LayoutConstants.DEFAULT_INCREMENTAL;
		public boolean defaultCreateBendsAsNeeded = LayoutConstants.DEFAULT_CREATE_BENDS_AS_NEEDED;
		public boolean defaultUniformLeafNodeSizes = LayoutConstants.DEFAULT_UNIFORM_LEAF_NODE_SIZES;
	}

	public class CoSE
	{
		public int idealEdgeLength; // any positive int
		public int springStrength; // 0-100
		public int repulsionStrength; // 0-100
		public boolean smartRepulsionRangeCalc; // T-F
		public int gravityStrength; // 0-100
		public int gravityRange; // 0-100
		public int compoundGravityStrength; // 0-100
		public int compoundGravityRange; // 0-100
		public boolean smartEdgeLengthCalc; // T-F
		public boolean multiLevelScaling; // T-F

		public int defaultIdealEdgeLength = CoSEConstants.DEFAULT_EDGE_LENGTH;
		public int defaultSpringStrength = 50;
		public int defaultRepulsionStrength = 50;
		public boolean defaultSmartRepulsionRangeCalc = CoSEConstants.DEFAULT_USE_SMART_REPULSION_RANGE_CALCULATION;
		public int defaultGravityStrength = 50;
		public int defaultGravityRange = 50;
		public int defaultCompoundGravityStrength = 50;
		public int defaultCompoundGravityRange = 50;
		public boolean defaultSmartEdgeLengthCalc = CoSEConstants.DEFAULT_USE_SMART_IDEAL_EDGE_LENGTH_CALCULATION;
		public boolean defaultMultiLevelScaling = CoSEConstants.DEFAULT_USE_MULTI_LEVEL_SCALING;
	}

	private LayoutOptionsPack()
	{
		general = new General();
		coSE = new CoSE();
		setDefaultLayoutProperties();
	}

	public void setDefaultLayoutProperties()
	{
		general.layoutQuality = general.defaultLayoutQuality ;
		general.animationDuringLayout = general.defaultAnimationDuringLayout ;
		general.animationOnLayout = general.defaultAnimationOnLayout ;
		general.animationPeriod = general.defaultAnimationPeriod;
		general.incremental = general.defaultIncremental ;
		general.createBendsAsNeeded = general.defaultCreateBendsAsNeeded ;
		general.uniformLeafNodeSizes = general.defaultUniformLeafNodeSizes ;

		coSE.idealEdgeLength = coSE.defaultIdealEdgeLength;
		coSE.springStrength = coSE.defaultSpringStrength ;
		coSE.repulsionStrength = coSE.defaultRepulsionStrength ;
		coSE.smartRepulsionRangeCalc = coSE.defaultSmartRepulsionRangeCalc ;
		coSE.gravityStrength = coSE.defaultGravityStrength ;
		coSE.gravityRange = coSE.defaultGravityRange ;
		coSE.compoundGravityStrength = coSE.defaultCompoundGravityStrength ;
		coSE.compoundGravityRange = coSE.defaultCompoundGravityRange ;
		coSE.smartEdgeLengthCalc = coSE.defaultSmartEdgeLengthCalc ;
		coSE.multiLevelScaling = coSE.defaultMultiLevelScaling ;
	}

	public static LayoutOptionsPack getInstance()
	{
		if (instance == null) {
			instance = new LayoutOptionsPack();
		}
		return instance;
	}


	public CoSE getCoSE()
	{
		return coSE;
	}


	public General getGeneral()
	{
		return general;
	}
}