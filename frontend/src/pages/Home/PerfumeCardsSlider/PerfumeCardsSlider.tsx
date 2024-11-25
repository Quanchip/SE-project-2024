import { Carousel, Typography } from "antd";
import { FC, ReactElement, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";

import { selectPerfumes } from "../../../redux-toolkit/perfumes/perfumes-selector";
import { resetPerfumesState } from "../../../redux-toolkit/perfumes/perfumes-slice";
import { fetchPerfumesByIds } from "../../../redux-toolkit/perfumes/perfumes-thunks";
import "./PerfumeCardsSlider.css";
import PerfumeCardsSliderItem from "./PerfumeCardsSliderItem/PerfumeCardsSliderItem";

export const perfumesIds = [26, 43, 46, 106, 34, 76, 82, 85, 27, 39, 79, 86];

const PerfumeCardsSlider: FC = (): ReactElement => {
    const dispatch = useDispatch();
    const perfumes = useSelector(selectPerfumes);

    useEffect(() => {
        // GraphQL example
        // dispatch(fetchPerfumesByIdsQuery(perfumesId));
        dispatch(fetchPerfumesByIds(perfumesIds));

        return () => {
            dispatch(resetPerfumesState());
        };
    }, [dispatch]);

    return (
        <div className={"perfume-cards-slider"}>
            <Typography.Title level={3} className={"perfume-cards-slider-title"}>
                PERSONALLY RECOMMENDED
            </Typography.Title>
            <Carousel>
                <PerfumeCardsSliderItem perfumes={perfumes.slice(0, 4)} />
                <PerfumeCardsSliderItem perfumes={perfumes.slice(4, 8)} />
                <PerfumeCardsSliderItem perfumes={perfumes.slice(8, 12)} />
            </Carousel>
        </div>
    );
};

export default PerfumeCardsSlider;
